import scala.scalajs.js.annotation.JSExportTopLevel

package object example {

  @JSExportTopLevel("initExample")
  def initExample(): Unit = {
    println("initExample()")
  }
}
