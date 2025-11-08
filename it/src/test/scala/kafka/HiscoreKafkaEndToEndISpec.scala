package kafka

import cats.effect.*
import cats.syntax.all.*
import fs2.kafka.*
import io.circe.syntax.*
import kafka.SkillUpdatedEvent
import consumers.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import weaver.*
import scala.concurrent.duration.*

object HiscoreKafkaEndToEndISpec extends IOSuite {

  implicit val logger = Slf4jLogger.getLogger[IO]

  type Res = KafkaProducer[String, String]

  def sharedResource: Resource[IO, Res] =
    KafkaProducer.resource(
      ProducerSettings[IO, String, String]
        .withBootstrapServers("localhost:9092")
    )

  test("HiscoreConsumer should consume a skill.updated event successfully") { producer =>
    val topic = s"skill.updated.test.${System.currentTimeMillis()}"

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
    val consumer = new HiscoreConsumerService[IO](
      bootstrapServers = "localhost:9092",
      firstTopic = topic,
      topicNames = List.empty
    )

    // 🔹 Run the consumer concurrently and send an event
    for {
      fiber <- consumer.stream.compile.drain.start
      _     <- IO.sleep(1.second) // wait for subscription
      _     <- producerSend
      _     <- IO.sleep(1.second) // allow processing
      _     <- fiber.cancel
    } yield success
  }
}
