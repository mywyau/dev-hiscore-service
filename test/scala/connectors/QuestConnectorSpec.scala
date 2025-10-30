package connectors

import cats.effect.IO
import cats.implicits.*
import cats.syntax.all.*
import cats.syntax.eq.*
import cats.Eq
import io.circe.generic.auto.*
import io.circe.syntax.*
import models.quests.*
import models.Demonic
import models.Open
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import weaver.SimpleIOSuite
import services.ConnectorSpecBase

object QuestConnectorSpec extends SimpleIOSuite with ConnectorSpecBase {

  val fakeQuestData =
    QuestData(
      questId = "QUEST001",
      clientId = "USER001",
      devId = Some("DEV001"),
      rank = Demonic,
      title = "Some fake title",
      description = Some("Fake Description"),
      acceptanceCriteria = Some("Acceptance Criteria"),
      status = Some(Open),
      tags = Seq("Tag1", "Tag2", "Tag3"),
      estimated = true
    )

  // --- helper to build fake client
  def fakeClient(response: Response[IO]): Client[IO] =
    Client.fromHttpApp(HttpApp[IO](_ => IO.pure(response)))

  test(".getQuestData() - should return Some(QuestData) on 200 OK") {

    val response = Response[IO](Status.Ok)
      .withEntity(fakeQuestData)(jsonEncoderOf[IO, QuestData])

    val client = fakeClient(response)
    val connector = QuestConnector[IO](client, uri"http://localhost:8082")

    connector.getQuestData("user123", "quest123").map { result =>
      assert(result.contains(fakeQuestData))
    }
  }

  test(".getQuestData() - should return None on 404 NotFound") {

    val response = Response[IO](Status.NotFound)
    val client = fakeClient(response)
    val connector = QuestConnector[IO](client, uri"http://localhost:8082")

    connector.getQuestData("user123", "missingQuest").map { result =>
      expect(result == None)
    }
  }

  test(".getQuestData() - should handle invalid JSON") {

    val response = Response[IO](Status.Ok)
      .withEntity("""{ "invalid": "data" }""")

    val client = fakeClient(response)
    val connector = QuestConnector[IO](client, uri"http://localhost:8082")

    connector.getQuestData("user123", "quest123").attempt.map {
      case Left(_) =>
        expect(true) // decoding failure expected
      case Right(data) =>
        expect(false)
    }
  }
}
