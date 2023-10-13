package app.http

import lib.zconfig.ZConfig
import lib.zdatasource.ZDataSource
import lib.zflyway.ZFlyway
import lib.zio.ZhttpExtensions.ZhttpOps
import lib.zlogger.ZLogger
import lib.zquill.ZQuill
import lib.zredis.ZRedis
import lib.zuuid.ZUUID
import modules.health.adapter.driver.HealthApi
import modules.health.core.CheckHealth
import modules.person.adapter.driven.{PersonCache, PersonQueue, PersonRepository}
import modules.person.adapter.driver.{PersonApi, PersonJobs}
import modules.person.core._
import modules.person.core.driven.PersonQueuePort
import zio._
import zio.config.typesafe.TypesafeConfigProvider
import zio.http._

case class HttpConfig(
    port: Int,
)

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    ZLogger.layer >>> Runtime.setConfigProvider(
      TypesafeConfigProvider.fromResourcePath(),
    )

  private val serverApis = (cfg: HttpConfig) =>
    ZIO.log("Starting Http application") *>
      Server
        .serve {
          {
            (HealthApi() ++ PersonApi()) @@ RequestHandlerMiddlewares.debug
          }.withDefaultErrorLogging.withDefaultErrorResponse
        }
        .provideSome[PersonQueuePort](
          Server.defaultWith {
            _.port(cfg.port)
              .keepAlive(true)
              .noIdleTimeout
              .logWarningOnFatalError(true)
          },
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

  private val serverJobs =
    ZIO.log("Starting background jobs") *>
      PersonJobs()
        .provideSome[PersonQueuePort](
          ZDataSource.layer,
          ZQuill.layer,
          PersonRepository.layer,
          CreatePersonBatch.layer,
        )
        .withParallelismUnbounded
        .tapDefect(d => ZIO.logFatal(s"$d"))
        .tapErrorCause(c => ZIO.logFatalCause(c))
        .fork *> ZIO.never

  private val serverArt =
    """
      |(   (       )    )
      | )\ ))\ ) ( /( ( /(  (
      |(()/(()/( )\()))\()) )\
      | /(_))(_)|(_)\((_)((((_)(
      |(_))(_))  _((_)_((_)\ _ )\
      || _ \_ _|| \| | || (_)_\(_)
      ||   /| | | .` | __ |/ _ \
      ||_|_\___||_|\_|_||_/_/ \_\
      |""".stripMargin

  override val run = {
    val start =
      for {
        _      <- ZIO.log(serverArt)
        zFlway <- ZIO.service[ZFlyway]
        _      <- zFlway.migrate
        config <- ZIO.config[HttpConfig](ZConfig.config.map(_.http))
        _      <- serverApis(config) <&> serverJobs
      } yield ()

    start.provide(
      ZDataSource.layer,
      ZFlyway.layer,
      // This ensure that the same queue in memory will be used by both fibers (api and jobs)
      PersonQueue.layer,
    )
  }

}
