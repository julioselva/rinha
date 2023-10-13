package lib.zlogger

import zio.logging.backend.SLF4J
import zio.{Runtime, ZLayer}

object ZLogger {

  val layer: ZLayer[Any, _, Unit] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

}
