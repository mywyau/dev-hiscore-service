package connectors

import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.parser.decode
import io.circe.Decoder
import models.users.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.Method.*
import org.typelevel.log4cats.Logger

final class AuthConnector[F[_] : Async : Logger](client: Client[F], baseUri: Uri) {

  def getUserProfile(userId: String): F[Option[UserData]] = {
    
    val request = Request[F](
      method = GET,
      uri = baseUri / "users" / userId
    )

    for {
      _ <- Logger[F].warn(s"[AuthConnector][getUserProfile] - Fetching user profile for $userId")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[UserData]
            .map(_.some)
        else
          Logger[F].warn(s"[AuthConnector][getUserProfile] Failed to fetch user: ${resp.status}") *>
            Async[F].pure(None)
      }
    } yield response
  }
}

object AuthConnector {
  def apply[F[_] : Async : Logger](client: Client[F], baseUri: Uri): AuthConnector[F] =
    new AuthConnector[F](client, baseUri)
}
