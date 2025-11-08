package connectors

import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.parser.decode
import io.circe.Decoder
import models.languages.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.Method.*
import org.typelevel.log4cats.Logger

trait LanguageConnectorAlgebra[F[_]] {

  def getLanguage(userId: String, language: Language): F[Option[LanguageData]]

  def getAllLanguageData(): F[List[LanguageData]]

  // def awardLanguageXP(
  //   devId: String,
  //   username: String,
  //   language: Language,
  //   xp: BigDecimal,
  //   level: Int,
  //   nextLevel: Int,
  //   nextLevelXp: BigDecimal
  // ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

final class LanguageConnector[F[_] : Async : Logger](client: Client[F], baseUri: Uri) {

  def getLanguageData(userId: String, language: Language): F[Option[LanguageData]] = {
    val request = Request[F](
      method = GET,
      uri = baseUri / "language" / userId
    )

    for {
      _ <- Logger[F].info(s"[LanguageConnector][getLanguageData] - Fetching Language data for $userId")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[LanguageData]
            .map(_.some)
        else
          Logger[F].warn(s"Failed to fetch user: ${resp.status}") *>
            Async[F].pure(None)
      }
    } yield response
  }

  def getAllLanguageData(): F[List[LanguageData]] = {
    val request = Request[F](
      method = GET,
      uri = baseUri / "language" / "all"
    )

    for {
      _ <- Logger[F].info(s"[LanguageConnector][getLanguageData] - Fetching all Language data")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[List[LanguageData]]
        else
          Logger[F].warn(s"Failed to fetch user: ${resp.status}") *>
            Async[F].pure(List())
      }
    } yield response
  }

  // def awardLanguageXP(
  //   devId: String,
  //   username: String,
  //   language: Language,
  //   xp: BigDecimal,
  //   level: Int,
  //   nextLevel: Int,
  //   nextLevelXp: BigDecimal
  // ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}

object LanguageConnector {
  def apply[F[_] : Async : Logger](client: Client[F], baseUri: Uri): LanguageConnector[F] =
    new LanguageConnector[F](client, baseUri)
}
