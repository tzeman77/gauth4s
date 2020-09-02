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

import scala.scalajs.js
import scala.scalajs.js.UndefOr

// https://developers.google.com/identity/sign-in/web/reference#users

@js.native
trait GoogleUser extends js.Object {
  def getId(): String = js.native
  def isSignedIn(): Boolean = js.native
  def getHostedDomain(): UndefOr[String] = js.native
  def getGrantedScopes(): String = js.native
  def getBasicProfile(): BasicProfile = js.native
  def getAuthResponse(includeAuthorizationData: Boolean = false): AuthResponse
}
