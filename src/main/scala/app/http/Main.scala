package app.http

import lib.zconfig.ZConfig
import lib.zdatasource.ZDataSource
import lib.zflyway.ZFlyway
import lib.zlogger.ZLogger
import modules.person.adapter.driven.PersonQueue
import zio._
import zio.config.typesafe.TypesafeConfigProvider

case class HttpConfig(
    port: Int,
)

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    ZLogger.layer >>> Runtime.setConfigProvider(
      TypesafeConfigProvider.fromResourcePath(),
    )

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

  override val run: ZIO[Any, Any, Unit] = {
    val start =
      for {
        _      <- ZIO.log(serverArt)
        zFlway <- ZIO.service[ZFlyway]
        _      <- zFlway.migrate
        config <- ZIO.config[HttpConfig](ZConfig.config.map(_.http))
        _      <- WarmUp.run(config)
        _      <- Api.provide(config) <&> Jobs.provide
      } yield ()

    start.provide(
      ZDataSource.layer,
      ZFlyway.layer,
      PersonQueue.layer,
    )
  }

}
