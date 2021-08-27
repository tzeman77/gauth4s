package example

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object ExampleApp extends App with Server with config with Urls {

  Http().newServerAt(server.interface, server.port).bind(
    Verifier.route ~ Index.route
  )

}
