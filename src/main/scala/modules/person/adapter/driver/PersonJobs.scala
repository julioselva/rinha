package modules.person.adapter.driver

import modules.person.core.driver.CreatingPersonBatch
import zio.{Schedule, ZIO, durationInt}

object PersonJobs {

  private val creatingPersonBatchSchedule = Schedule.fixed(1.seconds) && Schedule.spaced(1.seconds)

  def apply(): ZIO[CreatingPersonBatch, Nothing, Unit] =
    CreatingPersonBatch.exec.repeat(creatingPersonBatchSchedule).unit

}
