package controllers.mocks

import cats.effect._
import cats.syntax.all._
import connectors.*
import connectors.QuestConnector
import io.circe.Json
import models.payment.CheckoutSessionUrl
import models.payment.StripePaymentIntent
import org.typelevel.log4cats.Logger
import services.StripePaymentServiceAlgebra

// Mock Stripe service
class MockStripePaymentService[F[_] : Sync : Logger] extends StripePaymentServiceAlgebra[F] {

  override def createPaymentIntent(amount: Long, currency: String, devAccountId: String): F[StripePaymentIntent] =
    Logger[F].info("[MockStripePaymentService] createPaymentIntent called") >>
      StripePaymentIntent("secret_test").pure[F]

  override def createCheckoutSession(questId: String, clientId: String, developerStripeId: String, amount: Long, currency: String): F[CheckoutSessionUrl] =
    Logger[F].info("[MockStripePaymentService][createCheckoutSession] createCheckoutSession called") >>
      CheckoutSessionUrl("https://checkout.mock/session/test123").pure[F]

  override def verifyWebhook(payload: String, sigHeader: String): F[Json] =
    Logger[F].info("[MockStripePaymentService] verifyWebhook called") >>
      Json.obj("type" -> Json.fromString("payment_intent.succeeded")).pure[F]
}
