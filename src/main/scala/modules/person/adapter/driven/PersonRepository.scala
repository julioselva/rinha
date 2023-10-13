package modules.person.adapter.driven

import lib.zquill.ZQuill.ZQuill
import modules.person.core.driven.PersonRepositoryPort
import modules.person.infra.Person
import zio.{Chunk, ZIO, ZLayer}

import java.sql.SQLException
import java.util.UUID

class PersonRepositoryAdapter(quill: ZQuill) extends PersonRepositoryPort {

  import quill._

  override def checkNicknameAvailability(n: String): ZIO[Any, SQLException, Boolean] =
    run(query[Person].map(_.apelido).filter(_ == lift(n))).map(_.isEmpty)

  override def insert(person: Person): ZIO[Any, SQLException, Unit] =
    run(query[Person].insertValue(lift(person))).unit

  override def insertBatch(personBatch: Chunk[Person]): ZIO[Any, SQLException, List[Long]] =
    run(quote(liftQuery(personBatch.toList).foreach(p => query[Person].insertValue(p))), 1000)

  override def findById(id: UUID): ZIO[Any, SQLException, Option[Person]] =
    run(query[Person].take(1).filter(_.id == lift(id))).map(_.headOption)

  override def findByTerm(t: String): ZIO[Any, SQLException, List[Person]] =
    run(query[Person].take(50).filter(_ => sql"trgm_search".as[String] like lift(s"%$t%".toLowerCase)))

  override def count: ZIO[Any, SQLException, Int] =
    run(query[Person]).map(_.length)

}

object PersonRepository {

  val layer: ZLayer[ZQuill, Nothing, PersonRepositoryAdapter] =
    ZLayer.fromFunction(new PersonRepositoryAdapter(_))

}
