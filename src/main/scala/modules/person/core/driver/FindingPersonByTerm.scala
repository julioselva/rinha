package modules.person.core.driver

import modules.person.infra.Person
import zio.ZIO
import zio.macros.accessible

@accessible
trait FindingPersonByTerm {
  def exec(query: FindPersonByTermQuery): ZIO[Any, Nothing, List[Person]]
}

case class FindPersonByTermQuery(t: String)
