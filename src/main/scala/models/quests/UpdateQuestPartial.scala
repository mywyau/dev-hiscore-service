package models.quests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.Rank

case class UpdateQuestData(
  rank: Rank,
  title: String,
  description: Option[String],
  acceptanceCriteria: Option[String]
)

object UpdateQuestData {
  implicit val encoder: Encoder[UpdateQuestData] = deriveEncoder[UpdateQuestData]
  implicit val decoder: Decoder[UpdateQuestData] = deriveDecoder[UpdateQuestData]
}
