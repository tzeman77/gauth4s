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

import org.scalajs.dom

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.{Promise, Thenable}

// https://developers.google.com/identity/sign-in/web/reference#authentication

@js.native
trait GoogleAuth extends js.Object {
  def attachClickHandler(
    container: dom.Element,
    options: js.Object = new js.Object(),
    onSuccess: js.Function1[GoogleUser, Unit],
    onFailure: js.Function1[js.Dynamic, Unit]
  ): Unit = js.native

  def `then`[T](onInit: js.Function1[GoogleAuth, T],
    onError: js.Function1[js.Object, Unit]): Promise[T] = js.native

  def signOut(): Promise[Unit] = js.native

  def disconnect(): Promise[Unit] = js.native
}

object GoogleAuth {
  def initHelper(clientId: String): Future[GoogleAuth] = {
    val p = scala.concurrent.Promise[GoogleAuth]()
    gApi.load("auth2", () => {
      val auth2 = gAuth2.init(new ClientConfig(client_id = clientId))
      auth2.`then`(v => p.success(v),
        e => p.failure(new RuntimeException(e.toString)))
    })
    p.future
  }
}
