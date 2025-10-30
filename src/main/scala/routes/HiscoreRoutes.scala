package routes

import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import connectors.*
import controllers.*
import doobie.hikari.HikariTransactor
import java.net.URI
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import services.*
import services.LevelService

object HiscoreRoutes {

  def hiscoreRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](
    appConfig: AppConfig,
    skillsConnector: SkillsConnectorAlgebra[F],
    languageConnector: LanguageConnectorAlgebra[F]
  ): HttpRoutes[F] = {

    // val skillsConnector = SkillsConnector(client)
    // val languageConnector = LanguageConnector(client)

    val levelService = LevelService(skillsConnector, languageConnector)
    val hiscoreController = HiscoreController(levelService)

    hiscoreController.routes
  }
}
