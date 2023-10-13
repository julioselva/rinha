package modules.person.core

import lib.zuuid.ZUUID
import modules.person.core.driven.{PersonCachePort, PersonQueuePort}
import modules.person.core.driver.{CreatePersonCommand, CreatePersonE, CreatingPerson}
import modules.person.infra.Person
import zio.{Scope, ZIO, ZLayer}

import java.util.UUID

class CreatePerson(personCache: PersonCachePort, personQueue: PersonQueuePort) extends CreatingPerson {

  override def exec(cmd: CreatePersonCommand): ZIO[Scope with ZUUID, CreatePersonE, UUID] =
    for {
      _     <- getNickname(cmd.apelido)
      id    <- ZUUID.gen
      person = cmd.toPerson(id)
      _     <- cachePerson(person) <&> cacheNickname(person.apelido) <&> enqueuePerson(person)
    } yield id

  private def getNickname(nickname: String) =
    ZIO
      .fail(CreatePersonE.Conflict)
      .whenZIO(
        personCache
          .getNickname(nickname)
          .tapError(e => ZIO.logError(s"Error while checking nickname availability. Data: $nickname. Error: $e"))
          .orDie
          .map(_.isDefined),
      )

  private def cachePerson(person: Person) =
    personCache
      .set(person)
      .tapError(e => ZIO.logError(s"Error while caching person. Data: $person. Error: $e"))
      .orDie

  private def cacheNickname(nickname: String) =
    personCache
      .setNickname(nickname)
      .tapError(e => ZIO.logError(s"Error while caching nickname. Data: $nickname. Error: $e"))
      .orDie

  private def enqueuePerson(person: Person) =
    ZIO.ifZIO(personQueue.enqueue(person))(
      onTrue = ZIO.unit,
      onFalse = ZIO.logWarning(s"Cannot enqueue the following person: $person."),
    )

}

object CreatePerson {

  val layer: ZLayer[PersonCachePort with PersonQueuePort, Nothing, CreatePerson] =
    ZLayer.fromFunction(new CreatePerson(_, _))

}
