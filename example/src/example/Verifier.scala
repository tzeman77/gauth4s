package example

import java.time.Instant

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cz.functionals.gauth4s.GoogleAuthVerifier

object Verifier extends config with Urls {

  private def ts2s(ts: Long): String = Instant.ofEpochSecond(ts).toString

  val route: Route = (path(separateOnSlashes(VERIFY)) & post &
    cookie(GoogleAuthVerifier.G_CSRF_TOKEN) &
    formFields(GoogleAuthVerifier.CREDENTIAL,
      GoogleAuthVerifier.G_CSRF_TOKEN)) { (csrf1, tk, csrf2) =>
    complete { (csrf1.value == csrf2,
      GoogleAuthVerifier(gauth.clientId).verifyToken(tk)) match {
      case (true, Right(p)) =>
        HttpResponse(StatusCodes.OK,
        entity = HttpEntity(
          s"""
             |Audience: ${p.audience}
             |Email: ${p.email}
             |Email verified: ${p.emailVerified}
             |Expiration: ${p.expirationTimeSecs map ts2s}
             |Family name: ${p.familyName}
             |Given name: ${p.givenName}
             |Hosted domain: ${p.hostedDomain}
             |Issued at: ${p.issuedAtSecs map ts2s}
             |Issuer: ${p.issuer}
             |JWT Id: ${p.jwtId}
             |Locale: ${p.locale}
             |Name: ${p.name}
             |Not before: ${p.notBeforeSecs map ts2s}
             |Subject: ${p.subject}
             |Type: ${p.`type`}
             |""".stripMargin))
      case (true, Left(err)) =>
        HttpResponse(StatusCodes.Unauthorized,
          entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`,
            s"Verification error: ${err.toString}"))
      case (false, _) =>
        HttpResponse(StatusCodes.Unauthorized,
          entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`,
            s"Verification error: CSRF tokens mismatch"))
    }}}

}
