package connectors

import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.parser.decode
import models.quests.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait QuestConnectorAlgebra[F[_]] {

  def getQuestData(userId: String, questId: String): F[Option[QuestData]]

  def markPaid(questId: String): F[Option[QuestData]]

  def validateOwnership(questId: String, clientId: String): F[Option[QuestData]]
}

final class QuestConnector[F[_] : Async : Logger](client: Client[F], baseUri: Uri) extends QuestConnectorAlgebra[F] {

  override def getQuestData(userId: String, questId: String): F[Option[QuestData]] = {
    val request = Request[F](
      method = GET,
      uri = baseUri / "quest" / userId / questId
    )

    for {
      _ <- Logger[F].info(s"[QuestConnector][getQuestData] - Fetching quest data for - userId: $userId - questId: $questId")
      response <- client.run(request).use { resp =>
        if (resp.status.isSuccess)
          resp
            .asJsonDecode[QuestData]
            .map(_.some)
        else
          Logger[F].warn(s"[QuestConnector][getQuestData] Failed to fetch quest data: ${resp.status}") *>
            Async[F].pure(None)
      }
    } yield response
  }

  override def markPaid(questId: String): F[Option[QuestData]] =
    ???

  override def validateOwnership(questId: String, clientId: String): F[Option[QuestData]] =
    ???
}

object QuestConnector {
  def apply[F[_] : Async : Logger](client: Client[F], baseUri: Uri): QuestConnector[F] =
    new QuestConnector[F](client, baseUri)
}
