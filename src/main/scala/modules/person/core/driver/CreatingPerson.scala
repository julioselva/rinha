package modules.person.core.driver

import lib.zuuid.ZUUID
import modules.person.infra.Person
import zio.macros.accessible
import zio.{Scope, ZIO}

import java.time.LocalDate
import java.util.UUID

// ---- Port
@accessible
trait CreatingPerson {

  def exec(cmd: CreatePersonCommand): ZIO[Scope with ZUUID, CreatePersonE, UUID]
}

// ---- Command
case class CreatePersonCommand(
    apelido: String,
    nome: String,
    nascimento: LocalDate,
    stack: List[String],
) {

  def toPerson(id: UUID): Person =
    Person(id, apelido = apelido, nome = nome, nascimento, stack)

}

// ---- Errors
sealed trait CreatePersonE

object CreatePersonE {
  case object Conflict extends CreatePersonE
}
