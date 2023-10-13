package modules.person.infra.payloads.requests

import modules.person.core.driver.CreatePersonCommand
import zio.json.{DeriveJsonDecoder, JsonDecoder}

import java.time.LocalDate

case class CreatePersonRequest(
    apelido: Option[String],
    nome: Option[String],
    nascimento: LocalDate,
    stack: Option[List[String]],
) {

  def toCommand: Option[CreatePersonCommand] =
    for {
      ap <- apelido
      nm <- nome
      stk = stack.getOrElse(List.empty[String])
    } yield CreatePersonCommand(apelido = ap, nome = nm, nascimento = nascimento, stack = stk)

}

object CreatePersonRequest {

  implicit val decoder: JsonDecoder[CreatePersonRequest] = DeriveJsonDecoder.gen[CreatePersonRequest]

}
