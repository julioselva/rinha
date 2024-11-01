package app.http

import lib.zdatasource.ZDataSource
import lib.zio.ZhttpExtensions.ZhttpOps
import lib.zquill.ZQuill
import lib.zredis.ZRedis
import lib.zuuid.ZUUID
import modules.health.adapter.driver.HealthApi
import modules.health.core.CheckHealth
import modules.person.adapter.driven.{PersonCache, PersonRepository}
import modules.person.adapter.driver.PersonApi
import modules.person.core._
import modules.person.core.driven.PersonQueuePort
import zio._
import zio.http._

object Api {

  val provide: HttpConfig => ZIO[PersonQueuePort, Throwable, Nothing] = (cfg: HttpConfig) => {

    val serverLayer: ZLayer[Any, Throwable, Server] =
      Server.defaultWith {
        _.port(cfg.port)
          .keepAlive(true)
          .noIdleTimeout
          .logWarningOnFatalError(true)
      }

    ZIO.log("Starting Http application") *>
      Server
        .serve {
          (HealthApi() ++ PersonApi()).withDefaultErrorResponse.withDefaultErrorLogging
        }
        .provideSome[PersonQueuePort](
          serverLayer,
          Scope.default,
          ZUUID.layer,
          ZDataSource.layer,
          ZQuill.layer,
          ZRedis.layer,
          PersonCache.layer,
          CheckHealth.layer,
          CreatePerson.layer,
          FindPersonById.layer,
          FindPersonByTerm.layer,
          GetPersonLength.layer,
          PersonRepository.layer,
        )
  }

}
