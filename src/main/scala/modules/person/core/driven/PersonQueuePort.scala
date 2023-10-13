package modules.person.core.driven

import modules.person.infra.Person
import zio.{Chunk, ZIO}

trait PersonQueuePort {

  def enqueue(person: Person): ZIO[Any, Nothing, Boolean]

  def dequeue: ZIO[Any, Nothing, Chunk[Person]]
}
