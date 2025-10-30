package connectors

import cats.effect.IO
import cats.implicits.*
import cats.syntax.all.*
import cats.syntax.eq.*
import cats.Eq
import io.circe.generic.auto.*
import io.circe.syntax.*
import models.rewards.*
import models.Demonic
import models.NotPaid
import models.Open
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import services.ConnectorSpecBase
import weaver.SimpleIOSuite

object RewardsConnectorSpec extends SimpleIOSuite with ConnectorSpecBase {

  val fakeRewardData =
    RewardData(
      questId = "QUEST001",
      clientId = "CLIENT001",
      devId = Some("DEV001"),
      timeRewardValue = Some(100.00),
      completionRewardValue = Some(200.00),
      paid = NotPaid
    )

  // --- helper to build fake client
  def fakeClient(response: Response[IO]): Client[IO] =
    Client.fromHttpApp(HttpApp[IO](_ => IO.pure(response)))

  test(".getRewardsData() - should return Some(RewardData) on 200 OK") {

    val response = Response[IO](Status.Ok)
      .withEntity(fakeRewardData)(jsonEncoderOf[IO, RewardData])

    val client = fakeClient(response)
    val connector = RewardsConnector[IO](client, uri"http://localhost:8083")

    connector.getRewardsData("USER123").map { result =>
      assert(result.contains(fakeRewardData))
    }
  }

  test(".getRewardsData() - should return None on 404 NotFound") {

    val response = Response[IO](Status.NotFound)
    val client = fakeClient(response)
    val connector = RewardsConnector[IO](client, uri"http://localhost:8083")

    connector.getRewardsData("USER123").map { result =>
      expect(result == None)
    }
  }

  test(".getRewardsData() - should handle invalid JSON") {

    val response = Response[IO](Status.Ok)
      .withEntity("""{ "invalid": "data" }""")

    val client = fakeClient(response)
    val connector = RewardsConnector[IO](client, uri"http://localhost:8083")

    connector.getRewardsData("USER123").attempt.map {
      case Left(_) =>
        expect(true) // decoding failure expected
      case Right(data) =>
        expect(false)
    }
  }
}
