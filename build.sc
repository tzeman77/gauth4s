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
import mill.define.{Command, Sources, Target}
import mill.scalajslib._
import mill.scalalib._
import mill.scalalib.publish._

object V {
  val app = "0.3-SNAPSHOT"
  val scalaJs = "1.5.0"
  val scala212 = "2.12.12"
  val scala213 = "2.13.5"
}

object D {

  val autowire = ivy"com.lihaoyi::autowire::0.3.3"
  val boopickle = ivy"io.suzaku::boopickle::1.3.3"
  val configAnnotation = ivy"com.wacai::config-annotation:0.4.1"
  val googleApiClient = ivy"com.google.api-client:google-api-client:1.31.3"
  val scalaJsDom = ivy"org.scala-js::scalajs-dom::1.1.0"
  val scalatags = ivy"com.lihaoyi::scalatags::0.9.3"

  object akka {
    val http = ivy"com.typesafe.akka::akka-http:10.2.4"
    val stream = ivy"com.typesafe.akka::akka-stream:2.6.13"
  }

  object udash {
    val ver = "0.9.0-M8"
    val core = ivy"io.udash::udash-core::$ver"
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

  override def sources: Sources = T.sources(
    millSourcePath / 'src,
    millSourcePath / 'shared
  )

}

trait CommonJs extends Common with ScalaJSModule {
  override def scalaJSVersion: T[String] = V.scalaJs
}

class JvmModule(val crossScalaVersion: String) extends Common {

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(D.googleApiClient)
}

class JsModule(val crossScalaVersion: String) extends CommonJs {
  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(D.scalaJsDom)
}

object jvm extends Cross[JvmModule](V.scala212, V.scala213)
object js extends Cross[JsModule](V.scala212, V.scala213)

def publishLocal(): Command[Unit] = T.command{
  jvm(V.scala212).publishLocal()()
  js(V.scala212).publishLocal()()
  jvm(V.scala213).publishLocal()()
  js(V.scala213).publishLocal()()
  ()
}

def publishM2Local(p: os.Path): Command[Unit] = T.command{
  jvm(V.scala212).publishM2Local(p.toString)()
  js(V.scala212).publishM2Local(p.toString)()
  jvm(V.scala213).publishM2Local(p.toString)()
  js(V.scala213).publishM2Local(p.toString)()
  ()
}

object example extends Module {

  private val jsInit = "initExample"
  private val jsName = "example.js"
  private val resourceDir = "public"
  private val commonDeps = Agg(
    D.autowire, D.boopickle, D.scalatags
  )

  object jvm extends ScalaModule {
    override def scalaVersion: T[String] = V.scala213
    override def sources: Sources = T.sources(
      millSourcePath / 'src,
      millSourcePath / 'shared
    )
    override def ivyDeps: Target[Loose.Agg[Dep]] = commonDeps ++ Agg(
      D.configAnnotation,
      D.akka.http,
      D.akka.stream
    )
    override def moduleDeps = Seq(build.jvm(V.scala213))
    override def scalacOptions = T {
      super.scalacOptions() ++ Seq(
        "-Ymacro-annotations",
        s"-Xmacro-settings:conf.output.dir=${millSourcePath / 'resources}"
      )
    }
    override def mainClass: Target[Option[String]] = Some("example.ExampleApp")

    override def upstreamAssemblyClasspath: Target[Loose.Agg[PathRef]] = T{
      val d = T.ctx().dest
      os.copy(from = js.fastOpt().path,
        to = d / resourceDir / jsName,
        replaceExisting = true,
        createFolders = true)
      super.upstreamAssemblyClasspath() ++ Agg(PathRef(d))
    }

  }

  object js extends ScalaJSModule {
    override def scalaVersion: T[String] = V.scala213
    override def scalaJSVersion: T[String] = V.scalaJs
    override def sources: Sources = T.sources(
      millSourcePath / 'src,
      millSourcePath / 'shared
    )
    override def moduleDeps = Seq(build.js(V.scala213))
    override def ivyDeps: Target[Loose.Agg[Dep]] = commonDeps
  }
}

// vim: et ts=2 sw=2 syn=scala
