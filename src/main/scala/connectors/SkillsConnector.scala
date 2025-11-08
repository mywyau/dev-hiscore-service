package connectors

import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.parser.decode
import models.skills.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait SkillsConnectorAlgebra[F[_]] {

  def getSkill(userId: String, skill: Skill): F[Option[SkillData]]

  def getAllSkillData(): F[List[SkillData]]

  def awardSkillXP(
    devId: String,
    username: String,
    skill: Skill,
    xp: BigDecimal,
    level: Int,
    nextLevel: Int,
    nextLevelXp: BigDecimal
  ): F[List[SkillData]]
}

final class SkillsConnectorImpl[F[_] : Async : Logger](
  client: Client[F],
  baseUri: Uri
) {

  def getSkillsData(userId: String, skill: Skill): F[Option[SkillData]] = {
    val request = Request[F](
      method = GET,
      uri = baseUri / "skills" / userId
    )

    for {
      _ <- Logger[F].info(s"[SkillsConnector][getSkillsData] - Fetching Skills data for $userId")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[SkillData]
            .map(_.some)
        else
          Logger[F].warn(s"Failed to fetch user: ${resp.status}") *>
            Async[F].pure(None)
      }
    } yield response
  }

  def getAllSkillData(): F[List[SkillData]] = {

    val request = Request[F](
      method = GET,
      uri = baseUri / "skills" / "all"
    )

    for {
      _ <- Logger[F].info(s"[SkillsConnector][getSkillsData] - Fetching all Skills data")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[List[SkillData]]
        else
          Logger[F].warn(s"Failed to fetch user: ${resp.status}") *>
            Async[F].pure(List())
      }
    } yield response
  }

  def awardSkillXP(
    devId: String,
    username: String,
    skill: Skill,
    xp: BigDecimal,
    level: Int,
    nextLevel: Int,
    nextLevelXp: BigDecimal
  ): F[List[SkillData]] = ???

}

object SkillsConnector {
  def apply[F[_] : Async : Logger](client: Client[F], baseUri: Uri): SkillsConnectorImpl[F] =
    new SkillsConnectorImpl[F](client, baseUri)
}
