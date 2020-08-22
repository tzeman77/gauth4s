package example

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContextExecutor

trait Server {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = system.dispatcher
}

