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
import mill.modules.Jvm
import mill.scalajslib._
import mill.scalalib._
import mill.scalalib.publish._

object V {
  val app = "0.1-SNAPSHOT"
  val scalaJs = "0.6.33"
  val scala212 = "2.12.12"

}

object D {

  val autowire = ivy"com.lihaoyi::autowire::0.2.6"
  val boopickle = ivy"io.suzaku::boopickle::1.3.3"
  val configAnnotation = ivy"com.wacai::config-annotation:0.3.7"
  val googleApiClient = ivy"com.google.api-client:google-api-client:1.30.10"
  val macroParadise = ivy"org.scalamacros:::paradise:2.1.1"
  val scalaJsDom = ivy"org.scala-js::scalajs-dom::1.0.0"
  val scalatags = ivy"com.lihaoyi::scalatags:0.9.1"

  object akka {
    val http = ivy"com.typesafe.akka::akka-http:10.1.12"
    val stream = ivy"com.typesafe.akka::akka-stream:2.6.8"
  }

  object udash {
    val ver = "0.8.5"
    val core = ivy"io.udash::udash-core::$ver"
  }

}


trait Common extends CrossScalaModule with PublishModule {

  override def artifactName: T[String] = "gauth4s"

  override def publishVersion: Target[String] = V.app

  override def pomSettings = PomSettings(
    description = "Scala wrapper for Google Sign-In",
    organization = "cz.functionals",
    url = "",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl(developerConnection = Some(
      "ssh://tzeman@hg.functionals.cz/repos/public/gauth4s.fossil")),
    developers = Seq(
      Developer("tzeman", "Tomas Zeman", "")
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
    "-Xfuture",                          // Turn on future language features.
    "-target:jvm-1.8",
  )}

  override def sources: Sources = T.sources(
    millSourcePath / 'src,
    millSourcePath / 'shared
  )

  /*
  protected def commonDeps = Agg()

  override def ivyDeps: Target[Loose.Agg[Dep]] = commonDeps
  */

}

trait CommonJs extends Common with ScalaJSModule {
  override def scalaJSVersion: T[String] = V.scalaJs
}

class JvmModule(val crossScalaVersion: String) extends Common {

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(D.googleApiClient)
}

class JsModule(val crossScalaVersion: String) extends CommonJs

object jvm extends Cross[JvmModule](V.scala212)
object js extends Cross[JsModule](V.scala212)

def publishLocal(): Command[Unit] = T.command{
  jvm(V.scala212).publishLocal()()
  js(V.scala212).publishLocal()()
  ()
}

def publishM2Local(p: os.Path): Command[Unit] = T.command{
  jvm(V.scala212).publishM2Local(p.toString)()
  js(V.scala212).publishM2Local(p.toString)()
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
    def scalaVersion = V.scala212
    override def sources: Sources = T.sources(
      millSourcePath / 'src,
      millSourcePath / 'shared
    )
    override def ivyDeps: Target[Loose.Agg[Dep]] = commonDeps ++ Agg(
      D.configAnnotation,
      D.akka.http,
      D.akka.stream
    )
    override def moduleDeps = Seq(build.jvm(V.scala212))
    override def scalacPluginIvyDeps = Agg(D.macroParadise)
    override def scalacOptions = T {
      super.scalacOptions.map(_ :+
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
    def scalaVersion = V.scala212
    def scalaJSVersion = V.scalaJs
    override def sources: Sources = T.sources(
      millSourcePath / 'src,
      millSourcePath / 'shared
    )
    override def moduleDeps = Seq(build.js(V.scala212))
    override def ivyDeps: Target[Loose.Agg[Dep]] = commonDeps ++ Agg(
      D.scalaJsDom
    )
    override def scalacOptions: Target[Seq[String]] = T {
      super.scalacOptions.map(_ ++ Seq("-P:scalajs:sjsDefinedByDefault"))
    }
  }
}

// vim: et ts=2 sw=2 syn=scala
