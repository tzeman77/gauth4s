package example

import com.wacai.config.annotation.conf

@conf trait config {

  val server = new {
    val port = 8888
    val interface = "localhost"
  }

  val gauth = new {
    val clientId = "foobar"
    val platformJs = "https://apis.google.com/js/platform.js"
  }
}
