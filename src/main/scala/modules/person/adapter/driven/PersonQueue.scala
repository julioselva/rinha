package modules.person.adapter.driven

import modules.person.core.driven.PersonQueuePort
import modules.person.infra.Person
import zio._

class PersonQueueAdapter(personQueue: Queue[Person]) extends PersonQueuePort {

  override def enqueue(person: Person): ZIO[Any, Nothing, Boolean] =
    for {
      result <- personQueue.offer(person)
    } yield result

  override def dequeue: ZIO[Any, Nothing, Chunk[Person]] =
    for {
      result <- personQueue.takeAll
    } yield result

}

object PersonQueue {

  val layer: ZLayer[Any, Nothing, PersonQueueAdapter] =
    ZLayer {
      for {
        personQueue <- Queue.bounded[Person](30000)
      } yield new PersonQueueAdapter(personQueue)
    }

}
