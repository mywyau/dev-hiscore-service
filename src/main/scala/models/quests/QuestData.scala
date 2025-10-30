package models.quests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.Instant
import java.time.LocalDateTime
import models.languages.Language
import models.QuestStatus
import models.Rank

case class QuestData(
  questId: String,
  clientId: String,
  devId: Option[String],
  rank: Rank,
  title: String,
  description: Option[String],
  acceptanceCriteria: Option[String],
  status: Option[QuestStatus],
  tags: Seq[String],
  estimated: Boolean
)

object QuestData {
  implicit val encoder: Encoder[QuestData] = deriveEncoder[QuestData]
  implicit val decoder: Decoder[QuestData] = deriveDecoder[QuestData]
}
