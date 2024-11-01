package app.http

import lib.zdatasource.ZDataSource
import lib.zquill.ZQuill
import modules.person.adapter.driven.PersonRepository
import modules.person.adapter.driver.PersonJobs
import modules.person.core._
import modules.person.core.driven.PersonQueuePort
import zio._

object Jobs {

  val provide: ZIO[PersonQueuePort, Nothing, Nothing] =
    ZIO.log("Starting background jobs") *>
      PersonJobs()
        .provideSome[PersonQueuePort](
          ZDataSource.layer,
          ZQuill.layer,
          PersonRepository.layer,
          CreatePersonBatch.layer,
        )
        .withParallelismUnbounded
        .tapDefect(d => ZIO.logFatal(s"$d"))
        .tapErrorCause(c => ZIO.logFatalCause(c))
        .fork *> ZIO.never

}
