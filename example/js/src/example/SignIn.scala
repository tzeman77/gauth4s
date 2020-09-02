package example

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, global => g}
import scalatags.JsDom.all._

import scala.util.{Failure, Success}

object SignIn extends Urls {

  def initGoogleAuth(root: dom.Element, clientId: String): Unit = {
    g.gapi.load("auth2", () => {
      val auth2 = g.gapi.auth2.init(literal(
        client_id = clientId
      ))
      val btn = span(img(src:=s"$RESOURCES/google.png")).render
      root.appendChild(div("Sign in with: ", btn).render)
      auth2.attachClickHandler(btn, js.Object,
        (u: js.Dynamic) => {
          val p = u.getBasicProfile()
          val tk = u.getAuthResponse().id_token.toString
          println(s"ID: ${p.getId()}")
          println(s"Email: ${p.getEmail()}")
          println(s"token: $tk")
          Ajax.post(VERIFY, data = tk,
            headers = Map("Content-Type" -> "text/plain")) onComplete {
            case Success(r) => println(r.responseText)
            case Failure(e) => dom.window.alert(e.toString)
          }
        }, (e: js.Dynamic) => dom.window.alert(e.toString))
    })

  }

}
