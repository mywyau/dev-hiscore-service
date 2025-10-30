package connectors

import cats.effect.IO
import cats.implicits.*
import cats.syntax.all.*
import cats.syntax.eq.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import models.users.*
import models.Client
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import services.ConnectorSpecBase
import weaver.SimpleIOSuite

object AuthConnectorSpec extends SimpleIOSuite with ConnectorSpecBase {

  val fakeUserData =
    UserData(
      userId = "user123",
      username = "goku",
      email = "bob@gmail.com",
      firstName = Some("bob"),
      lastName = Some("smith"),
      userType = Some(models.Client)
    )

  // --- helper to build fake client
  def fakeClient(response: Response[IO]): Client[IO] =
    org.http4s.client.Client.fromHttpApp(HttpApp[IO](_ => IO.pure(response)))

  test(".getUserProfile() - should return Some(UserData) on 200 OK") {

    val response = Response[IO](Status.Ok)
      .withEntity(fakeUserData)(jsonEncoderOf[IO, UserData])

    val client = fakeClient(response)
    val connector = AuthConnector[IO](client, uri"http://localhost:8081")

    connector.getUserProfile("user123").map { result =>
      assert(result.contains(fakeUserData))
    }
  }

  test(".getUserProfile() - should return None on 404 NotFound") {

    val response = Response[IO](Status.NotFound)
    val client = fakeClient(response)
    val connector = AuthConnector[IO](client, uri"http://localhost:8081")

    connector.getUserProfile("user123").map { result =>
      expect(result == None)
    }
  }

  test(".getUserProfile() - should handle invalid JSON") {

    val response = Response[IO](Status.Ok)
      .withEntity("""{ "invalid": "data" }""")

    val client = fakeClient(response)
    val connector = AuthConnector[IO](client, uri"http://localhost:8081")

    connector.getUserProfile("user123").attempt.map {
      case Left(_) =>
        expect(true) // decoding failure expected
      case Right(data) =>
        expect(false)
    }
  }
}
