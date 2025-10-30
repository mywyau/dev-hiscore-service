package connectors

import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.parser.decode
import io.circe.Decoder
import models.rewards.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.Method.*
import org.typelevel.log4cats.Logger

final class RewardsConnector[F[_] : Async : Logger](
  client: Client[F],
  baseUri: Uri
) {

  def getRewardsData(userId: String): F[Option[RewardData]] = {
    val request = Request[F](
      method = GET,
      uri = baseUri / "rewards" / userId
    )

    for {
      _ <- Logger[F].info(s"[RewardsConnector][getRewardsData] - Fetching rewards data for $userId")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[RewardData]
            .map(_.some)
        else
          Logger[F].warn(s"Failed to fetch user: ${resp.status}") *>
            Async[F].pure(None)
      }
    } yield response
  }
}

object RewardsConnector {
  def apply[F[_] : Async : Logger](client: Client[F], baseUri: Uri): RewardsConnector[F] =
    new RewardsConnector[F](client, baseUri)
}
