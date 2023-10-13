package lib.zconfig

import app.http.HttpConfig
import lib.zdatasource.DataSourceConfig
import zio.Config
import zio.config._
import zio.config.magnolia._
import zio.redis.RedisConfig

case class ApplicationConf(
    http: HttpConfig,
    datasource: DataSourceConfig,
    redis: RedisConfig,
)

object ZConfig {

  val config: Config[ApplicationConf] = deriveConfig[ApplicationConf].mapKey(toKebabCase)

}
