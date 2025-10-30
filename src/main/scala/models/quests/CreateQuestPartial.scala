package models.quests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.languages.Language
import models.Rank

case class CreateQuestData(
  rank: Rank,
  title: String,
  description: Option[String],
  acceptanceCriteria: String,
  tags: Seq[Language]
)

object CreateQuestData {
  implicit val encoder: Encoder[CreateQuestData] = deriveEncoder[CreateQuestData]
  implicit val decoder: Decoder[CreateQuestData] = deriveDecoder[CreateQuestData]
}
