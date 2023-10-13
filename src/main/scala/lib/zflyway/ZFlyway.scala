package lib.zflyway

import lib.zdatasource.ZDataSource.ZDataSource
import org.flywaydb.core.Flyway
import zio.macros.accessible
import zio.{ZIO, ZLayer}

@accessible
trait ZFlyway {
  def migrate: ZIO[Any, Throwable, Unit]
}

class ZFlywayImpl(flyway: Flyway) extends ZFlyway {

  override def migrate: ZIO[Any, Throwable, Unit] =
    ZIO
      .from(flyway.migrate())
      .zipLeft(ZIO.log(s"Migrations applied successfully"))
      .unit

}

object ZFlyway {

  val layer: ZLayer[ZDataSource, Nothing, ZFlywayImpl] =
    ZLayer {
      for {
        zDataSource <- ZIO.service[ZDataSource]
        flyway       = Flyway.configure().dataSource(zDataSource).validateMigrationNaming(true).load()
      } yield new ZFlywayImpl(flyway)
    }

}
