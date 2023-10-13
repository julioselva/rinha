package modules.person.core

import modules.person.core.driven.{PersonQueuePort, PersonRepositoryPort}
import modules.person.core.driver.CreatingPersonBatch
import modules.person.infra.Person
import zio.{Chunk, ZIO, ZLayer}

class CreatePersonBatch(personQueue: PersonQueuePort, personRepository: PersonRepositoryPort)
    extends CreatingPersonBatch {

  override def exec: ZIO[Any, Nothing, Unit] =
    for {
      personBatch <- personQueue.dequeue
      _           <- insertBatch(personBatch)
    } yield ()

  private def insertBatch(personBatch: Chunk[Person]) =
    personRepository
      .insertBatch(personBatch)
      .tapError(e => ZIO.logError(s"Error while inserting a batch of person. Data: $personBatch. Error: $e"))
      .orDie

}

object CreatePersonBatch {

  val layer: ZLayer[PersonQueuePort with PersonRepositoryPort, Nothing, CreatePersonBatch] =
    ZLayer.fromFunction(new CreatePersonBatch(_, _))

}
