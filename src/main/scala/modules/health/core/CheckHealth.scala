package modules.health.core

import modules.health.core.driver.CheckingHealth
import zio.{URIO, ZIO, ZLayer}

class CheckHealth extends CheckingHealth {
  override def exec: URIO[Any, Unit] = ZIO.unit
}

object CheckHealth {

  val layer: ZLayer[Any, Nothing, CheckingHealth] =
    ZLayer.succeed(new CheckHealth)

}
