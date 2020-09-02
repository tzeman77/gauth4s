package example

import cz.functionals.gauth4s.{ClientConfig, GoogleAuth, gApi, gAuth2}
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scalatags.JsDom.all._

object SignIn extends Urls {

  def initGoogleAuth(root: dom.Element, clientId: String): Unit = {
    GoogleAuth.initHelper(clientId) foreach { auth2 =>
      val btn = span(img(src:=s"$RESOURCES/google.png")).render
      val result = div().render
      root.appendChild(div(div(
        label(verticalAlign.top, lineHeight:="3em", "Sign in with: "),
        btn), hr, result).render)
      auth2.attachClickHandler(btn,
        onSuccess = u => {
          btn.style.display = display.none.v

          result.appendChild(pre(s"""User:
             |id: ${u.getId()}
             |signed in: ${u.isSignedIn()}
             |hd: ${u.getHostedDomain()}
             |scopes: ${u.getGrantedScopes()}
             |""".stripMargin).render)

          val p = u.getBasicProfile()
          result.appendChild(pre(s"""Profile:
             |id: ${p.getId()}
             |name: ${p.getName()}
             |given name: ${p.getGivenName()}
             |family name: ${p.getFamilyName()}
             |image url: ${p.getImageUrl()}
             |email: ${p.getEmail()}
             |""".stripMargin).render)

          val aur = u.getAuthResponse()
          result.appendChild(pre(s"""AuthResp:
               |access_token: ${aur.access_token}
               |id_token: ${aur.id_token}
               |scope: ${aur.scope}
               |expires in: ${aur.expires_in}
               |issued: ${aur.first_issued_at}
               |expires at: ${aur.expires_at}
               |""".stripMargin).render)

          Ajax.post(VERIFY, data = aur.id_token,
            headers = Map("Content-Type" -> "text/plain")) onComplete {
            case Success(r) => result.appendChild(pre(
              s"Server-side authentication: ${r.responseText}").render)
              val logout = button("Logout").render
              logout.addEventListener("click", (e: Event) => {
                auth2.disconnect().toFuture.foreach(_ => {
                  dom.window.location.reload(flag = true)
                })
              })
              result.appendChild(logout)
            case Failure(e) => dom.window.alert(e.toString)
          }
        },
        onFailure = e => dom.window.alert(e.toString)
      )
    }
  }

}
