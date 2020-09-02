import scala.scalajs.js.annotation.JSExportTopLevel

import org.scalajs.dom

package object example {

  @JSExportTopLevel("initExample")
  def initExample(domId: String, clientId: String): Unit = {
    Option(dom.document.getElementById(domId)) match {
      case Some(el) =>
        SignIn.initGoogleAuth(el, clientId)
      case None =>
        dom.window.alert("Element not found.")
    }
  }
}
