/*
 * Copyright 2020-2021 Tomas Zeman <tomas@functionals.cz>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * REPL:
 *
 * ./mill --repl -w
 *
 * Generate Idea project:
 *
 * ./mill mill.scalalib.GenIdea/idea
 *
 */ 

import mill._
import mill.api.Loose
import mill.define.{Command, Target}
import mill.scalalib._
import mill.scalalib.publish._

object V {
  val app = "0.3"
  val scala212 = "2.12.14"
  val scala213 = "2.13.6"
}

object D {

  val configAnnotation = ivy"com.wacai::config-annotation:0.4.2"
  val googleApiClient = ivy"com.google.api-client:google-api-client:1.32.1"
  val scalatags = ivy"com.lihaoyi::scalatags::0.9.4"

  object akka {
    val http = ivy"com.typesafe.akka::akka-http:10.2.6"
    val stream = ivy"com.typesafe.akka::akka-stream:2.6.16"
  }

}


trait Common extends CrossScalaModule with PublishModule {

  override def artifactName: T[String] = "gauth4s"

  override def publishVersion: Target[String] = V.app

  override def pomSettings: T[PomSettings] = PomSettings(
    description = "Scala wrapper for Google Sign-In",
    organization = "cz.functionals",
    url = "https://fossil.functionals.cz/gauth4s",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl(developerConnection = Some(
      "ssh://tzeman@fossil.functionals.cz/repos/public/gauth4s.fossil")),
    developers = Seq(
      Developer("tzeman", "Tomas Zeman", "https://functionals.cz")
    )
  )

  override def scalacOptions = T{Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-language:reflectiveCalls",
    "-language:postfixOps",
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-target:jvm-1.8"
  )}

}

class JvmModule(val crossScalaVersion: String) extends Common {

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(D.googleApiClient)
}

object jvm extends Cross[JvmModule](V.scala212, V.scala213)

def publishLocal(): Command[Unit] = T.command{
  jvm(V.scala212).publishLocal()()
  jvm(V.scala213).publishLocal()()
  ()
}

def publishM2Local(p: os.Path): Command[Unit] = T.command{
  jvm(V.scala212).publishM2Local(p.toString)()
  jvm(V.scala213).publishM2Local(p.toString)()
  ()
}

object example extends ScalaModule {

  override def scalaVersion: T[String] = V.scala213
  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    D.configAnnotation,
    D.scalatags,
    D.akka.http,
    D.akka.stream
  )
  override def moduleDeps = Seq(jvm(V.scala213))
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-Ymacro-annotations",
      s"-Xmacro-settings:conf.output.dir=${millSourcePath / 'resources}"
    )
  }
  override def mainClass: Target[Option[String]] = Some("example.ExampleApp")

}

// vim: et ts=2 sw=2 syn=scala
