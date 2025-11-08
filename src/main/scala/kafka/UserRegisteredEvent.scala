package kafka

import java.time.Instant
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

final case class UserRegisteredEvent(
  userId: String,
  username: String,
  email: String,
  userType: String,
  createdAt: Instant
)

object UserRegisteredEvent {
  implicit val decoder: Decoder[UserRegisteredEvent] = deriveDecoder
  implicit val encoder: Encoder[UserRegisteredEvent] = deriveEncoder
}
