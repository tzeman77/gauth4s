package example

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cz.functionals.gauth4s.GoogleAuthVerifier

object Verifier extends config with Urls {

  val route: Route = (path(separateOnSlashes(VERIFY)) & post & entity(as[String])) { tk =>
    complete(GoogleAuthVerifier(gauth.clientId).verifyToken(tk) match {
      case Right(p) => HttpResponse(StatusCodes.OK,
        entity = HttpEntity(p.toString))
      case Left(err) => HttpResponse(StatusCodes.Unauthorized,
        entity = HttpEntity(err.toString))
    })
  }

}
