package modules.person.core.driven

import modules.person.infra.Person
import zio.ZIO
import zio.redis.RedisError

import java.util.UUID

trait PersonCachePort {
  def set(person: Person): ZIO[Any, RedisError, Unit]
  def get(id: UUID): ZIO[Any, RedisError, Option[Person]]
  def setNickname(nickname: String): ZIO[Any, RedisError, Unit]
  def getNickname(nickname: String): ZIO[Any, RedisError, Option[String]]
}
