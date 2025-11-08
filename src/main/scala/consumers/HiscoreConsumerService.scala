package consumers

import cats.effect.Async
import cats.syntax.all.*
import fs2.kafka.*
import io.circe.parser.decode
import org.typelevel.log4cats.Logger
import kafka.{SkillUpdatedEvent, LanguageUpdatedEvent}
import dev.profunktor.redis4cats.RedisCommands

class HiscoreConsumerService[F[_]: Async: Logger](
  bootstrapServers: String,
  firstTopic: String,
  topicNames: List[String]
) {

  private val settings =
    ConsumerSettings[F, String, String]
      .withBootstrapServers(bootstrapServers)
      .withGroupId("hiscores-consumer")
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

  def stream: fs2.Stream[F, Unit] =
    KafkaConsumer
      .stream(settings)
      .subscribeTo(firstTopic = firstTopic, remainingTopics = topicNames*)
      .records
      .evalMap { msg =>
        val json = msg.record.value
        msg.record.topic match {

          case "skill.updated" =>
            decode[SkillUpdatedEvent](json) match {
              case Right(evt) =>
                updateLeaderboard(evt.userId, evt.xp, evt.level, "skills") >>
                  msg.offset.commit
              case Left(err) =>
                Logger[F].error(s"Failed to decode skill.updated: ${err.getMessage}")
            }

          case "language.updated" =>
            decode[LanguageUpdatedEvent](json) match {
              case Right(evt) =>
                updateLeaderboard(evt.userId, evt.xp, evt.level, "languages") >>
                  msg.offset.commit
              case Left(err) =>
                Logger[F].error(s"Failed to decode language.updated: ${err.getMessage}")
            }

          case other =>
            Logger[F].warn(s"Ignored event from topic $other")
        }
      }

  private def updateLeaderboard(userId: String, xp: Int, level: Int, category: String): F[Unit] =
    for {
      _ <- Logger[F].info(s"Updating leaderboard [$category] for user $userId -> XP=$xp, level=$level")
    } yield ()
}
