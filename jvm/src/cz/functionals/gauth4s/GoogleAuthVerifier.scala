/*
 * Copyright 2020 Tomas Zeman <tomas@functionals.cz>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.functionals.gauth4s

import java.util.Collections

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import cz.functionals.gauth4s.GoogleAuthVerifier.{Err, VerificationFailed}

import scala.collection.JavaConverters._
import scala.language.implicitConversions

class GoogleAuthVerifier(verifier: GoogleIdTokenVerifier) {

  implicit def jl2opt(v: java.lang.Long): Option[Long] = Option(v)
  implicit def s2opt(v: String): Option[String] = Option(v)
  implicit def any2opt(v: AnyRef): Option[String] = Option(v) map(_.toString)

  def verifyToken(token: String): Either[Err, ProfileInfo] = {
    verifier.verify(token) match {
      case null => Left(VerificationFailed)
      case t =>
        val p = t.getPayload
        Right(ProfileInfo(
          expirationTimeSecs = p.getExpirationTimeSeconds,
          notBeforeSecs = p.getNotBeforeTimeSeconds,
          issuedAtSecs = p.getIssuedAtTimeSeconds,
          issuer = p.getIssuer,
          jwtId = p.getJwtId,
          `type` = p.getType,
          subject = p.getSubject,
          audience = p.getAudienceAsList.asScala.toList,
          hostedDomain = p.getHostedDomain,
          email = p.getEmail,
          emailVerified = p.getEmailVerified,
          name = p.get("name"),
          familyName = p.get("family_name"),
          givenName = p.get("given_name"),
          locale = p.get("locale")
        ))
    }
  }
}

object GoogleAuthVerifier {
  sealed trait Err
  case object VerificationFailed extends Err

  def apply(clientId: String): GoogleAuthVerifier = new GoogleAuthVerifier(
    new GoogleIdTokenVerifier.Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance)
      .setAudience(Collections.singletonList(clientId))
      .build())
}
