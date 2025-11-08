package kafka

import io.circe.generic.semiauto.*
import io.circe.Decoder
import io.circe.Encoder

final case class LanguageUpdatedEvent(
  userId: String,
  language: String,
  xp: Int,
  level: Int
)

object LanguageUpdatedEvent {
  implicit val decoder: Decoder[LanguageUpdatedEvent] = deriveDecoder
  implicit val encoder: Encoder[LanguageUpdatedEvent] = deriveEncoder
}
