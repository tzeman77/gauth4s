package example

import com.wacai.config.annotation.conf

@conf trait config {

  val server = new {
    val port = 8888
    val interface = "localhost"
  }

}
