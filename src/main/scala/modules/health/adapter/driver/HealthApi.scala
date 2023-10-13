package modules.health.adapter.driver

import modules.health.core.driver.CheckingHealth
import zio.http._

object HealthApi {

  type HealthApi = CheckingHealth

  def apply(): HttpApp[HealthApi, Nothing] = Http.collectZIO[Request] { case Method.GET -> Root / "health" =>
    CheckingHealth.exec.as(Response.ok)
  }

}
