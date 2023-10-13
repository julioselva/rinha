package modules.person.core.driver

import zio.ZIO
import zio.macros.accessible

@accessible
trait CreatingPersonBatch {
  def exec: ZIO[Any, Nothing, Unit]
}
