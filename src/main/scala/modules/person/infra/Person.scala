package modules.person.infra

import zio.json._
import zio.schema.{DeriveSchema, Schema}

import java.time.LocalDate
import java.util.UUID

final case class Person(
    id: UUID,
    apelido: String,
    nome: String,
    nascimento: LocalDate,
    stack: List[String],
    trgmSearch: String,
)

object Person {

  implicit val personJsonEncoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
  implicit val personJsonDecoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]

  implicit val personSchema: Schema[Person] = DeriveSchema.gen[Person]

  def apply(
    id: UUID,
    apelido: String,
    nome: String,
    nascimento: LocalDate,
    stack: List[String],
  ): Person = {
    val trgmSearch: String = (List(apelido, nome) ++ stack).mkString(" ").toLowerCase()
    Person(id, apelido = apelido, nome = nome, nascimento, stack, trgmSearch)
  }

}
