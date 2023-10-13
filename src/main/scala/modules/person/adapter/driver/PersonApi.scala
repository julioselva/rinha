package modules.person.adapter.driver

import lib.zuuid.ZUUID
import modules.person.core.driver._
import modules.person.infra.Person._
import modules.person.infra.payloads.requests.CreatePersonRequest
import zio.http._
import zio.http.codec.TextCodec.UUIDCodec
import zio.json.{DecoderOps, EncoderOps}
import zio.{Scope, ZIO}

object PersonApi {

  type PersonApi = Scope
    with ZUUID
    with CreatingPerson
    with FindingPersonById
    with FindingPersonByTerm
    with GettingPersonLength

  def apply(): HttpApp[PersonApi, Nothing] = Http
    .collectZIO[Request] {
      case req @ Method.POST -> Root / "pessoas" =>
        def handleErrors(e: CreatePersonE) = e match {
          case CreatePersonE.Conflict => Response.status(Status.UnprocessableEntity)
        }

        req.body.asString.orDie.map(_.fromJson[CreatePersonRequest]).flatMap {
          case Left(_)     => ZIO.succeed(Response.status(Status.BadRequest))
          case Right(body) =>
            body.toCommand match {
              case Some(cmd) =>
                CreatingPerson
                  .exec(cmd)
                  .map(id => Response(Status.Created, Headers("Location", s"/pessoas/$id")))
                  .mapError(handleErrors)
                  .merge
              case None      =>
                ZIO.succeed(Response.status(Status.UnprocessableEntity))
            }

        }

      case Method.GET -> Root / "pessoas" / UUIDCodec(id) =>
        FindingPersonById.exec(FindPersonByIdQuery(id)).map {
          case Some(person) => Response.json(person.toJson)
          case None         => Response.status(Status.NotFound)
        }

      case req @ Method.GET -> Root / "pessoas" =>
        req.url.queryParams.get("t").map(_.asString) match {
          case None       => ZIO.succeed(Response.status(Status.BadRequest))
          case Some(term) =>
            FindingPersonByTerm
              .exec(FindPersonByTermQuery(term))
              .map(r => Response.json(r.toJson))
        }

      case Method.GET -> Root / "contagem-pessoas" => GettingPersonLength.exec.map(l => Response.text(s"$l"))
    }

}
