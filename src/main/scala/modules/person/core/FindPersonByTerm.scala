package modules.person.core

import modules.person.core.driven.PersonRepositoryPort
import modules.person.core.driver.{FindPersonByTermQuery, FindingPersonByTerm}
import modules.person.infra.Person
import zio.{ZIO, ZLayer}

class FindPersonByTerm(personRepository: PersonRepositoryPort) extends FindingPersonByTerm {

  override def exec(query: FindPersonByTermQuery): ZIO[Any, Nothing, List[Person]] =
    personRepository
      .findByTerm(query.t)
      .tapError(e => ZIO.logError(s"Error while finding persons by term. Data: $query. Error: $e"))
      .orDie

}

object FindPersonByTerm {

  val layer: ZLayer[PersonRepositoryPort, Nothing, FindPersonByTerm] =
    ZLayer.fromFunction(new FindPersonByTerm(_))

}
