import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "TimesMe"
  val appVersion = "1.0"

  val appDependencies = Seq(
    "com.restfb" % "restfb" % "1.6.14",
    "ws.securesocial" %% "securesocial" % "2.1.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions += "-feature",
    resolvers += Resolver.sonatypeRepo("releases")
  )
}