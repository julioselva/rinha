package modules.person.core.driver

import zio.ZIO
import zio.macros.accessible

@accessible
trait GettingPersonLength {
  def exec: ZIO[Any, Nothing, Int]
}
