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

// https://developers.google.com/identity/sign-in/web/reference#gapiauth2authresponse

@js.native
trait AuthResponse extends js.Object {
  val access_token: UndefOr[String] = js.native
  val id_token: String = js.native
  val scope: UndefOr[String] = js.native
  val expires_in: Number = js.native
  val first_issued_at: Number = js.native
  val expires_at: Number = js.native
}
