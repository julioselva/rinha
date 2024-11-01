package modules.person.adapter.driver

import modules.person.core.driver.CreatingPersonBatch
import zio._

object PersonJobs {

  private val creatingPersonBatchSchedule =
    Schedule.fixed(500.milliseconds)

  def apply(): ZIO[CreatingPersonBatch, Nothing, Unit] =
    CreatingPersonBatch.exec.repeat(creatingPersonBatchSchedule).unit

}
