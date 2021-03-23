package example

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object ExampleApp extends App with Server with config with Urls {

  private val assets = pathPrefix(ASSETS) {
    getFromResourceDirectory("META-INF/resources/webjars")
  }

  private val resources = pathPrefix(RESOURCES) {
    getFromResourceDirectory("public")
  }

  Http().newServerAt(server.interface, server.port).bind(
    assets ~ resources ~ Verifier.route ~ Index.route
  )

}
