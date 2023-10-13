package lib.zio

import zio._
import zio.http._

object ZhttpExtensions {

  implicit class ZhttpOps[-R, +Err, -In, +Out](zhttp: Http[R, Err, In, Out]) {

    def withDefaultErrorLogging(implicit trace: Trace, ev1: Request <:< In, ev2: Out <:< Response): App[R] =
      zhttp
        .tapErrorZIO(e => ZIO.logError(s"Internal server error: $e"))
        .tapErrorCauseZIO(c => ZIO.logCause(c))
        .asInstanceOf[App[R]]

  }

}
