package modules.person.core

import modules.person.core.driven.PersonRepositoryPort
import modules.person.core.driver.GettingPersonLength
import zio.{ZIO, ZLayer}

class GetPersonLength(personRepository: PersonRepositoryPort) extends GettingPersonLength {

  override def exec: ZIO[Any, Nothing, Int] =
    personRepository.count.orDie

}

object GetPersonLength {

  val layer: ZLayer[PersonRepositoryPort, Nothing, GetPersonLength] =
    ZLayer.fromFunction(new GetPersonLength(_))

}
