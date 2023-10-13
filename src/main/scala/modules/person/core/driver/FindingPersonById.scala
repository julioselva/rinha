package modules.person.core.driver

import modules.person.infra.Person
import zio.ZIO
import zio.macros.accessible

import java.util.UUID

@accessible
trait FindingPersonById {
  def exec(query: FindPersonByIdQuery): ZIO[Any, Nothing, Option[Person]]
}

case class FindPersonByIdQuery(id: UUID)
