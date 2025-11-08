package kafka

import io.circe.generic.semiauto.*
import io.circe.Decoder
import io.circe.Encoder

final case class SkillUpdatedEvent(
  userId: String,
  skillName: String,
  xp: Int,
  level: Int
)

object SkillUpdatedEvent {
  implicit val decoder: Decoder[SkillUpdatedEvent] = deriveDecoder
  implicit val encoder: Encoder[SkillUpdatedEvent] = deriveEncoder
}
