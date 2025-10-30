import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*
import cats.NonEmptyParallel
import com.comcast.ip4s.*
import configuration.AppConfig
import configuration.ConfigReader
import connectors.QuestConnector
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import fs2.Stream
import infrastructure.Database
import infrastructure.KafkaProducerProvider
import infrastructure.Redis
import infrastructure.Server
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import middleware.Middleware.throttleMiddleware
import modules.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.implicits.*
import org.http4s.server.middleware.CORS
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.HttpRoutes
import org.http4s.Method
import org.http4s.Uri
import org.typelevel.ci.CIString
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import repositories.*
import routes.Routes.*
import scala.concurrent.duration.*
import scala.concurrent.duration.DurationInt
import services.*

object Main extends IOApp {

  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  override def run(args: List[String]): IO[ExitCode] = {

    val serverResource: Resource[IO, Server] = for {
      appConfig <- Resource.eval(ConfigReader[IO].loadAppConfig)
      transactor <- DatabaseModule.make[IO](appConfig)
      redis <- RedisModule.make[IO](appConfig)
      httpClient <- HttpClientModule.make[IO]
      questConnector = QuestConnector[IO](httpClient, uri"http://localhost:8082")
      httpApp <- HttpModule.make(
        appConfig,
        httpClient,
        transactor,
        questConnector
      )
      host <- Resource.eval(IO.fromOption(Host.fromString(appConfig.serverConfig.host))(new RuntimeException("Invalid host in configuration")))
      port <- Resource.eval(IO.fromOption(Port.fromInt(appConfig.serverConfig.port))(new RuntimeException("Invalid port in configuration")))
      server <- EmberServerBuilder
        .default[IO]
        .withHost(host)
        .withPort(port)
        .withHttpApp(httpApp)
        .build
    } yield server

    // Run the server forever
    serverResource.use(_ => IO.never).as(ExitCode.Success)
  }
}
