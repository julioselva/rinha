package app.http

import lib.zdatasource.ZDataSource
import lib.zquill.ZQuill
import lib.zredis.ZRedis
import lib.zuuid.ZUUID
import modules.person.adapter.driven.{PersonCache, PersonQueue, PersonRepository}
import modules.person.adapter.driver.PersonApi
import modules.person.core.{CreatePerson, FindPersonById, FindPersonByTerm, GetPersonLength}
import zio.http._
import zio.{Random, _}

object WarmUp {

  private val provide: HttpConfig => Task[Nothing] = (cfg: HttpConfig) =>
    ZIO.log("Starting Warm Up application") *>
      Server
        .serve(PersonApi())
        .provide(
          Server.defaultWithPort(cfg.port),
          Scope.default,
          ZUUID.layer,
          ZDataSource.layer,
          ZQuill.layer,
          ZRedis.layer,
          PersonCache.layer,
          CreatePerson.layer,
          FindPersonById.layer,
          FindPersonByTerm.layer,
          GetPersonLength.layer,
          PersonRepository.layer,
          PersonQueue.layer,
        )

  private def warmUpRequests(cfg: HttpConfig) = {

    val createRequest = {
      val genBody: UIO[Body] =
        for {
          apelido <- Random.nextString(8)
          nome    <- Random.nextString(8)
        } yield Body.fromString(
          s"""{"apelido":"$apelido", "nome":"$nome", "nascimento": "1987-11-16", "stack": ["scala"]}""",
        )

      val request = (body: Body) =>
        ZIO
          .fromEither(URL.decode(s"http://localhost:${cfg.port}/pessoas"))
          .flatMap(url => Client.request(Request.post(body, url)))

      for {
        body     <- genBody
        response <- request(body)
        _        <- ZIO.log(s"Warm up create request completed with status: ${response.status}")
      } yield ()
    }
    val queryRequest  =
      ZIO
        .fromEither(URL.decode(s"http://localhost:${cfg.port}/pessoas"))
        .flatMap(url => Client.request(Request.get(url)))
        .tap(response => ZIO.log(s"Warm up query request completed with status: ${response.status}"))

    val validSearchRequest =
      ZIO
        .fromEither(URL.decode(s"http://localhost:${cfg.port}/pessoas?t=scala"))
        .flatMap(url => Client.request(Request.get(url)))
        .tap(response => ZIO.log(s"Warm up valid search request completed with status: ${response.status}"))

    val invalidSearchRequest =
      ZIO
        .fromEither(URL.decode(s"http://localhost:${cfg.port}/pessoas?t=anything"))
        .flatMap(url => Client.request(Request.get(url)))
        .tap(response => ZIO.log(s"Warm up invalid search request completed with status: ${response.status}"))

    for {
      _ <- createRequest
      _ <- queryRequest
      _ <- validSearchRequest
      _ <- invalidSearchRequest
    } yield ()
  }

  def run(config: HttpConfig): UIO[Unit] = {
    val pipe =
      for {
        _          <- ZIO.log("Warming up")
        warmupApis <- provide(config).fork
        _          <- ZIO.sleep(2.seconds)
        _          <- warmUpRequests(config)
        _          <- warmupApis.interrupt
      } yield ()

    pipe.provide(Client.default).orDie
  }

}
