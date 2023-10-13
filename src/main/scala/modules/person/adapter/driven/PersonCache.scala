package modules.person.adapter.driven

import lib.zredis.ZRedis.ZRedis
import modules.person.core.driven.PersonCachePort
import modules.person.infra.Person
import modules.person.infra.Person.personSchema
import zio.redis.RedisError
import zio.{ZIO, ZLayer}

import java.util.UUID

class PersonCacheAdapter(redis: ZRedis) extends PersonCachePort {

  override def set(person: Person): ZIO[Any, RedisError, Unit] =
    redis.set(s"person.${person.id}", person).unit

  override def get(id: UUID): ZIO[Any, RedisError, Option[Person]] =
    redis.get(s"person.$id").returning[Person]

  override def setNickname(nickname: String): ZIO[Any, RedisError, Unit] =
    redis.set(s"nickname.$nickname", nickname).unit

  override def getNickname(nickname: String): ZIO[Any, RedisError, Option[String]] =
    redis.get(s"nickname.$nickname").returning[String]

}

object PersonCache {

  val layer: ZLayer[ZRedis, Nothing, PersonCacheAdapter] =
    ZLayer.fromFunction(new PersonCacheAdapter(_))

}
