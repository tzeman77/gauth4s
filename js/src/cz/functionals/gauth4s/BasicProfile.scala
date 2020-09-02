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

// https://developers.google.com/identity/sign-in/web/reference#googleusergetbasicprofile

@js.native
trait BasicProfile extends js.Object {
  def getId(): String = js.native
  def getName(): String = js.native
  def getGivenName(): String = js.native
  def getFamilyName(): String = js.native
  def getImageUrl(): String = js.native
  def getEmail(): String = js.native
}
