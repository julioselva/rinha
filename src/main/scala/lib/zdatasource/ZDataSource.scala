package lib.zdatasource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import lib.zconfig.ZConfig
import zio.{ZIO, ZLayer}

case class DataSourceConfig(
    user: String,
    password: String,
    host: String,
    port: String,
)

object ZDataSource {

  type ZDataSource = HikariDataSource

  private def makeDataSourceConfig(cfg: DataSourceConfig) = {
    val hikariConfig: HikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(s"jdbc:postgresql://${cfg.host}:${cfg.port}/rinha")
    hikariConfig.setUsername(cfg.user)
    hikariConfig.setPassword(cfg.password)

    hikariConfig
  }

  private val config: ZLayer[Any, Nothing, DataSourceConfig] =
    ZLayer(ZIO.config[DataSourceConfig](ZConfig.config.map(_.datasource))).orDie

  val layer: ZLayer[Any, Nothing, ZDataSource] =
    config
      .project(makeDataSourceConfig)
      .project(new HikariDataSource(_))

}
