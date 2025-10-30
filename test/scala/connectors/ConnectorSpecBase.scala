package services

import cats.effect.IO
import cats.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait ConnectorSpecBase {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

}
