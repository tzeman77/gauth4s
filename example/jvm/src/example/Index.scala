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

  lazy val domId = "my-signin2"

  def render: String = "<!DOCTYPE html>" + html(
    all.head(
      meta(charset:="utf-8"),
      meta(name:="viewport", content:="width=device-width, initial-scale=1.0"),
      meta(name:="google-signin-client_id", content:=gauth.clientId),
      tags2.title("Google authentication example")
    ),
    body(margin:="5em",
      h1("Google authentication example"),
      div(id:=domId),
      script(
        s"""
          |function onSuccess(googleUser) {
          |  console.log('Logged in as: ' + googleUser.getBasicProfile().getName());
          |    var profile = googleUser.getBasicProfile();
          |    var id_token = googleUser.getAuthResponse().id_token;
          |    console.log('ID: ' + profile.getId());
          |    console.log('Full Name: ' + profile.getName());
          |    console.log('Given Name: ' + profile.getGivenName());
          |    console.log('Family Name: ' + profile.getFamilyName());
          |    console.log('Image URL: ' + profile.getImageUrl());
          |    console.log('Email: ' + profile.getEmail());
          |    console.log('id_token: ' + id_token);
          |}
          |function onFailure(error) {
          |  console.log(error);
          |}
          |function renderButton() {
          |  gapi.signin2.render('$domId', {
          |    'scope': 'profile email',
          |    'width': 240,
          |    'height': 50,
          |    'longtitle': true,
          |    'theme': 'dark',
          |    'onsuccess': onSuccess,
          |    'onfailure': onFailure
          |  });
          |}
          |""".stripMargin),
      script(src:=s"${gauth.platformJs}?onload=renderButton",
        attr("async").empty, defer)
    )
  )
}

