import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "optionometer"
  val appVersion      = "1.0.1"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "com.github.nscala-time" %% "nscala-time" % "0.6.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.2"
  )

}
