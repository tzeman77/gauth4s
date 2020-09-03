package example

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scalatags.Text.all._
import scalatags.Text.{all, tags2}

object Index extends config with Urls {

  val route: Route = (pathEnd | pathSingleSlash | pathPrefix("")) {
    complete(HttpResponse(StatusCodes.OK,
      entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, render)))
  }

  val domId = "main"
  val resultId = "result"

  def render: String = "<!DOCTYPE html>" + html(
    all.head(
      meta(charset:="utf-8"),
      meta(name:="viewport", content:="width=device-width, initial-scale=1.0"),
      tags2.title("Google authentication example")
    ),
    body(margin:="5em",
      h1("Google authentication example"),
      div(id:=domId),
      script(src:="https://apis.google.com/js/api:client.js"),
      script(tpe:="text/javascript", src:=s"/$jsBundle"),
      script(raw(s"initExample('$domId', '${gauth.clientId}');"))
    )
  )
}

