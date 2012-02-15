import sbt._
import Keys._

object ProjectBuild extends Build {
  lazy val root   = Project(id = "skycastle",        base = file(".")) aggregate(common, client, server)
  lazy val client = Project(id = "skycastle-client", base = file("client"))dependsOn(common)
  lazy val server = Project(id = "skycastle-server", base = file("server"))dependsOn(common)
  lazy val common = Project(id = "skycastle-common", base = file("common"))
}