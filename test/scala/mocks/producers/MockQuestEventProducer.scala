// package mocks.producers

// import cats.data.Validated.Valid
// import cats.data.ValidatedNel
// import cats.effect.IO
// import fs2.Stream
// import java.time.Instant
// import models.database.*
// import models.events.QuestCreatedEvent
// import models.kafka.KafkaProducerResult
// import models.kafka.SuccessfulWrite
// import models.quests.*
// import models.work_time.HoursOfWork
// import models.QuestStatus
// import models.Rank

// case class MockQuestEventProducer() extends QuestEventProducerAlgebra[IO] {

//   override def publishQuestCreated(event: QuestCreatedEvent): IO[KafkaProducerResult] = IO(SuccessfulWrite)
// }
