package example

import java.util.Collections

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

object Verifier extends config with Urls {

  val route: Route = (path(separateOnSlashes(VERIFY)) & post & entity(as[String])) { tk =>
    complete(verify(tk))
  }

  private def verify(tk: String) = {
    val transport = GoogleNetHttpTransport.newTrustedTransport();
    val verifier = new GoogleIdTokenVerifier.Builder(
      transport, JacksonFactory.getDefaultInstance)
      .setAudience(Collections.singletonList(gauth.clientId))
      .build()
    val idToken = Option(verifier.verify(tk))
    for {
      t <- idToken
    } yield {
      val payload = t.getPayload
      val userId = payload.getSubject
      val email = payload.getEmail
      val emailVerified = payload.getEmailVerified
      val name = payload.get("name")
      val familyName = payload.get("family_name")
      val givenName = payload.get("given_name")
      val locale = payload.get("locale")
      val hd = payload.getHostedDomain
      s"""
         |userId: $userId
         |email: $email, verified: $emailVerified
         |name: $name
         |family name: $familyName
         |given name: $givenName
         |locale: $locale
         |hosted domain: $hd
         |
         |aud: ${payload.getAudienceAsList.toArray mkString ", "}
         |auth: ${payload.getAuthorizationTimeSeconds}
         |auth party: ${payload.getAuthorizedParty}
         |exp: ${payload.getExpirationTimeSeconds}
         |issued at: ${payload.getIssuedAtTimeSeconds}
         |issuer: ${payload.getIssuer}
         |jwtId: ${payload.getJwtId}
         |not before: ${payload.getNotBeforeTimeSeconds}
         |type: ${payload.getType}
         |""".stripMargin
    }

  }
}
