package controllers.mocks

import cats.effect.*
import cats.syntax.all.*
import connectors.*
import connectors.QuestConnector
import io.circe.Json
import models.Demonic
import models.Open
import models.payment.CheckoutSessionUrl
import models.payment.StripePaymentIntent
import models.quests.QuestData
import org.typelevel.log4cats.Logger
import services.StripePaymentService

// Mock QuestConnector
class MockQuestConnector[F[_] : Sync : Logger] extends QuestConnectorAlgebra[IO] {

  override def getQuestData(userId: String, questId: String): IO[Option[QuestData]] = ???

  override def markPaid(questId: String): IO[Option[QuestData]] = ???

  override def validateOwnership(questId: String, clientId: String): IO[Option[QuestData]] =
    IO(
      Some(
        QuestData(
          questId = "QUEST001",
          clientId = "CLIENT001",
          devId = Some("DEV001"),
          rank = Demonic,
          title = "Some fake title",
          description = Some("Fake Description"),
          acceptanceCriteria = Some("Acceptance Criteria"),
          status = Some(Open),
          tags = Seq("Tag1", "Tag2", "Tag3"),
          estimated = true
        )
      )
    )

}
