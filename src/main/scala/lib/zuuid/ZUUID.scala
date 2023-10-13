package lib.zuuid

import zio._
import zio.macros.accessible

import java.util.UUID

@accessible
trait ZUUID {
  def gen: UIO[UUID]
}

object ZUUID {

  val layer: ULayer[ZUUID] = ZLayer.succeed(
    new ZUUID {
      override def gen: UIO[UUID] = ZIO.succeed(UUID.randomUUID())
    },
  )

}
