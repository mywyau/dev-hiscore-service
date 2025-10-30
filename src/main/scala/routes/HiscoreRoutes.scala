package routes

import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import controllers.*
import doobie.hikari.HikariTransactor
import java.net.URI
import org.http4s.HttpRoutes
import services.*
import services.LevelService
import connectors.*
import services.*

object HiscoreRoutes {

  def hiscoreRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](
    appConfig: AppConfig,
    transactor: HikariTransactor[F]
  ): HttpRoutes[F] = {

    val skillsConnector = SkillsConnector()
    val languageConnector = LanguageConnector()
    val levelService = LevelService(skillsConnector, languageConnector)

    val hiscoreController = HiscoreController(levelService)

    hiscoreController.routes
  }
}
