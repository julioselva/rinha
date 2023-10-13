package modules.person.core

import modules.person.core.driven.{PersonCachePort, PersonRepositoryPort}
import modules.person.core.driver.{FindPersonByIdQuery, FindingPersonById}
import modules.person.infra.Person
import zio.{ZIO, ZLayer}

import java.util.UUID

class FindPersonById(personCache: PersonCachePort, personRepository: PersonRepositoryPort) extends FindingPersonById {

  override def exec(query: FindPersonByIdQuery): ZIO[Any, Nothing, Option[Person]] =
    ZIO.collectFirst(List(findOnCache(query.id), findOnDatabase(query.id)))(x => x)

  private def findOnCache(id: UUID) =
    personCache
      .get(id)
      .tapError(e => ZIO.logError(s"Error while finding person by id on cache. Data: $id. Error: $e"))
      .orDie

  private def findOnDatabase(id: UUID) =
    personRepository
      .findById(id)
      .tapError(e => ZIO.logError(s"Error while finding person by id on database. Data: $id. Error: $e"))
      .orDie

}

object FindPersonById {

  val layer: ZLayer[PersonCachePort with PersonRepositoryPort, Nothing, FindPersonById] =
    ZLayer.fromFunction(new FindPersonById(_, _))

}
