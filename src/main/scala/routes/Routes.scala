package routes

import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import connectors.QuestConnector
import controllers.*
import doobie.hikari.HikariTransactor
import infrastructure.cache.SessionCache
import infrastructure.cache.SessionCacheImpl
import java.net.URI
import org.http4s.client.Client
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
// import repositories.*
import services.*

object Routes {

  def baseRoutes[F[_] : Concurrent : Logger](): HttpRoutes[F] = {

    val baseController = BaseController()

    baseController.routes
  }

  def paymentRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](
    appConfig: AppConfig,
    client: Client[F],
    questConnector: QuestConnector[F],
    transactor: HikariTransactor[F]
  ): HttpRoutes[F] = {

    val sessionCache = new SessionCacheImpl(appConfig)
    val stripePaymentService = StripePaymentService(appConfig, client)
    val paymentService = LivePaymentServiceImpl(questConnector, stripePaymentService)
    val paymentController = new PaymentControllerImpl(paymentService, sessionCache)

    paymentController.routes
  }

}
