import sbt._
import com.github.retronym.OneJarProject

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with OneJarProject {

//  override def mainClass = Some("org.foo.MainObj")

  // Scala unit testing
  val scalatest = "org.scalatest" % "scalatest" % "1.3"

  // Simplex libs
//  def simplex3dJars = descendents("lib" / "simplex3d", "*.jar")

  // Logging
  def loggingJars = descendents("lib" / "logging", "*.jar")

  override def unmanagedClasspath = super.unmanagedClasspath +++ loggingJars
  
}
