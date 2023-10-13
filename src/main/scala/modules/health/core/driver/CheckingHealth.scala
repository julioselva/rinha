package modules.health.core.driver

import zio.URIO
import zio.macros.accessible

@accessible
trait CheckingHealth {
  def exec: URIO[Any, Unit]
}
