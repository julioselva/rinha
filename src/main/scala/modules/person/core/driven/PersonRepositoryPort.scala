package modules.person.core.driven

import modules.person.infra.Person
import zio.{Chunk, ZIO}

import java.sql.SQLException
import java.util.UUID

trait PersonRepositoryPort {
  def checkNicknameAvailability(n: String): ZIO[Any, SQLException, Boolean]
  def insert(person: Person): ZIO[Any, SQLException, Unit]
  def insertBatch(personBatch: Chunk[Person]): ZIO[Any, SQLException, List[Long]]
  def findById(id: UUID): ZIO[Any, SQLException, Option[Person]]
  def findByTerm(t: String): ZIO[Any, SQLException, List[Person]]
  def count: ZIO[Any, SQLException, Int]
}
