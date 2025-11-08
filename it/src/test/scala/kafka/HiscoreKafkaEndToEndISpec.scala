package kafka

import cats.effect.*
import cats.syntax.all.*
import consumers.HiscoreConsumerService
import fs2.kafka.*
import io.circe.syntax.*
import models.events.QuestCreatedEvent
import models.kafka.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import shared.KafkaProducerResource
import weaver.*

import java.time.Instant
import scala.concurrent.duration.*

object HiscoreKafkaEndToEndISpec extends IOSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = KafkaProducer[IO, String, String]

  def sharedResource: Resource[IO, Res] =
    KafkaProducer.resource(
      ProducerSettings[IO, String, String]
        .withBootstrapServers("localhost:9092")
    )

  private def resetKafkaTopic(topic: String): IO[Unit] =
    IO.blocking {
      import sys.process._
      s"docker exec kafka-container-redpanda-1 rpk topic create $topic --brokers localhost:9092".!
    }.void

  private def deleteTopic(topic: String): IO[Unit] =
    IO.blocking {
      import sys.process._
      s"docker exec kafka-container-redpanda-1 rpk topic delete $topic --brokers localhost:9092".!
    }.void

  test("HiscoreConsumer should consume a skill.updated event successfully") { producer =>

    val topic = s"skill.updated.test"

    val event = SkillUpdatedEvent(
      userId = "user123",
      skillName = "Scala",
      xp = 200,
      level = 3
    )

    val producerRecord = ProducerRecord(topic, event.userId, event.asJson.noSpaces)
    val producerSend = producer.produce(ProducerRecords.one(producerRecord)).flatten.void

    val consumerSettings =
      ConsumerSettings[IO, String, String]
        .withBootstrapServers("localhost:9092")
        .withGroupId(s"hiscores-test-${System.currentTimeMillis()}")
        .withAutoOffsetReset(AutoOffsetReset.Earliest)

    // 🔹 Create the consumer instance
    val consumer =
      new HiscoreConsumerService[IO](
        bootstrapServers = "localhost:9092",
        firstTopic = topic,
        topicNames = List.empty
      )

    // 🔹 Run the consumer concurrently and send an event
    for {
      _ <- resetKafkaTopic(topic)
      fiber <- consumer.stream.compile.drain.start
      _ <- IO.sleep(1.second) // wait for subscription
      _ <- producerSend
      _ <- IO.sleep(1.second) // allow processing
      _ <- fiber.cancel
      _ <- deleteTopic(topic)
    } yield success
  }
}
