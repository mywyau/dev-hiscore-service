package controllers

import cats.effect.*
import controllers.ControllerISpecBase
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import io.circe.Json
import java.time.LocalDateTime
import models.*
import models.database.*
import models.payment.CheckoutPaymentPayload
import models.payment.CheckoutSessionUrl
import models.responses.*
import models.users.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.http4s.Method.*
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

class PaymentControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /dev-irl-client-payment-service/payment/health -  health check should return the health response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/dev-irl-client-payment-service/payment/health")

    client.run(request).use { response =>
      response.as[GetResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == GetResponse("/dev-irl-client-payment-service/payment/health", "I am alive - PaymentController")
        )
      }
    }
  }

  test(
    "GET - /dev-irl-client-payment-service/stripe/checkout - should return a checkout session url if the client id and quest id is valid"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val sessionToken = "test-session-token"

    def requestBody(): CheckoutPaymentPayload =
      CheckoutPaymentPayload(
        developerStripeId = "developerStripeId",
        amountCents = 10000
      )

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/dev-irl-client-payment-service/stripe/checkout/CLIENT001/QUEST001")
        .addCookie("auth_session", sessionToken)
        .withEntity(requestBody().asJson)

    client.run(request).use { response =>
      response.as[CheckoutSessionUrl].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == CheckoutSessionUrl("https://checkout.mock/session/test123")
        )
      }
    }
  }

  test(
    "POST - /dev-irl-client-payment-service/stripe/checkout - should return a checkout session url if the client id and quest id is valid"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val sessionToken = "test-session-token"

    def requestBody(): CheckoutPaymentPayload =
      CheckoutPaymentPayload(
        developerStripeId = "developerStripeId",
        amountCents = 10000
      )

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/dev-irl-client-payment-service/stripe/checkout/CLIENT001/QUEST001")
        .addCookie("auth_session", sessionToken)
        .withEntity(requestBody().asJson)

    client.run(request).use { response =>
      response.as[CheckoutSessionUrl].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == CheckoutSessionUrl("https://checkout.mock/session/test123")
        )
      }
    }
  }
}
