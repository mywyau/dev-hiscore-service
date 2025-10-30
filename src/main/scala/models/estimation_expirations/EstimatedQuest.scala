package models.estimation_expirations

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.Instant
import models.quests.QuestData

case class EstimatedQuest(
  quest: QuestData,
  estimationCloseAt: Option[Instant]
)

object EstimatedQuest {
  implicit val encoder: Encoder[EstimatedQuest] = deriveEncoder[EstimatedQuest]
  implicit val decoder: Decoder[EstimatedQuest] = deriveDecoder[EstimatedQuest]
}
