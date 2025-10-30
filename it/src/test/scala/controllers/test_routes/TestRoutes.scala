package controllers.test_routes

import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.*
import cats.implicits.*
import cats.Applicative
import cats.NonEmptyParallel
import configuration.AppConfig
import configuration.BaseAppConfig
import connectors.QuestConnector
import controllers.mocks.*
import controllers.BaseController
import controllers.PaymentControllerImpl
import dev.profunktor.redis4cats.RedisCommands
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import fs2.kafka.*
import infrastructure.cache.*
import infrastructure.KafkaProducerProvider
import java.net.URI
import java.time.Duration
import java.time.Instant
import models.auth.UserSession
import models.events.QuestCreatedEvent
import models.kafka.KafkaProducerResult
import models.kafka.SuccessfulWrite
import modules.HttpClientModule
import org.http4s.client.Client
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.http4s.Uri
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.*
import services.*
import services.stripe.*

object TestRoutes extends BaseAppConfig {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def fakeUserSession(userId: String) = {
    val sessionToken = "test-session-token"
    UserSession(
      userId = userId,
      cookieValue = sessionToken,
      email = s"$userId@gmail.com",
      userType = "Dev"
    )
  }

  // val mockQuestConnector = new MockQuestConnector()
  // val mockStripePaymentService = new MockStripePaymentService()

  def baseRoutes(): HttpRoutes[IO] = {
    val baseController = BaseController[IO]()
    baseController.routes
  }

  def mockAuthCachedSessions =
    Ref.of[IO, Map[String, UserSession]](
      Map(
        s"auth:session:USER001" -> fakeUserSession("USER001"),
        s"auth:session:USER002" -> fakeUserSession("USER002"),
        s"auth:session:USER003" -> fakeUserSession("USER003"),
        s"auth:session:USER004" -> fakeUserSession("USER004"),
        s"auth:session:USER005" -> fakeUserSession("USER005"),
        s"auth:session:USER006" -> fakeUserSession("USER006"),
        s"auth:session:USER008" -> fakeUserSession("USER008"),
        s"auth:session:USER007" -> fakeUserSession("USER007"),
        s"auth:session:USER009" -> fakeUserSession("USER009"),
        s"auth:session:USER010" -> fakeUserSession("USER010"),
        s"auth:session:CLIENT001" -> fakeUserSession("CLIENT001")
      )
    )

  def paymentRoutes(
    appConfig: AppConfig,
    transactor: HikariTransactor[IO]
  ): Resource[IO, HttpRoutes[IO]] =
    for {
      mockedSessionRef <- Resource.eval(mockAuthCachedSessions)
      mockSessionCache = new MockSessionCache(mockedSessionRef)
      mockQuestConnector = new MockQuestConnector[IO]
      mockStripePaymentService = new MockStripePaymentService[IO]
      paymentService = new LivePaymentServiceImpl(mockQuestConnector, mockStripePaymentService)
      paymentController = new PaymentControllerImpl(paymentService, mockSessionCache)
    } yield paymentController.routes

  def createTestRouter(appConfig: AppConfig, transactor: HikariTransactor[IO]): Resource[IO, HttpRoutes[IO]] =
    for {
      paymentRoutes <- paymentRoutes(appConfig, transactor)
    } yield Router(
      "/dev-irl-hiscore-service" -> (
        baseRoutes() <+> paymentRoutes
      )
    )
}
