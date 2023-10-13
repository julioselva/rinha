package lib.zquill

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import lib.zdatasource.ZDataSource.ZDataSource
import zio.{ZIO, ZLayer}

object ZQuill {

  type ZQuill = Quill.Postgres[SnakeCase.type]

  val layer: ZLayer[ZDataSource, Throwable, ZQuill] =
    ZLayer {
      for {
        zDataSource <- ZIO.service[ZDataSource]
      } yield Quill.DataSource.fromDataSource(zDataSource) >>> Quill.Postgres.fromNamingStrategy(SnakeCase)
    }.flatten

}
