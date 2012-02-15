
name := "skycastle-server"

version := "0.1"

scalaVersion := "2.9.1"


resolvers += "Akka Repository" at "http://akka.io/repository"

resolvers += "Guicefruit Repository" at "http://guiceyfruit.googlecode.com/svn/repo/releases"

resolvers += "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"


libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.4"

libraryDependencies += "log4j" % "log4j" % "1.2.16"

libraryDependencies += "com.google.inject" % "guice" % "3.0"

libraryDependencies += "redis.clients" % "jedis" % "2.0.0"

libraryDependencies += "com.dyuproject.protostuff" % "protostuff-api" % "1.0.4"

libraryDependencies += "com.dyuproject.protostuff" % "protostuff-runtime" % "1.0.4"

libraryDependencies += "com.dyuproject.protostuff" % "protostuff-core" % "1.0.4"

libraryDependencies += "com.dyuproject.protostuff" % "protostuff-collectionschema" % "1.0.4"

// Akka for parallel signals and messaging
libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor"       % "1.1.3",
  "se.scalablesolutions.akka" % "akka-slf4j"       % "1.1.3",
  "se.scalablesolutions.akka" % "akka-typed-actor" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-amqp"        % "1.1.3",
  "se.scalablesolutions.akka" % "akka-testkit"     % "1.1.3"
)

// JMonkey Engine 3.0 for 3D gfx
libraryDependencies ++= Seq(// Core lib
  // Third party libs
  "com.jme3" % "eventbus" % "3.0.0.20120209-SNAPSHOT",
  "com.jme3" % "stack-alloc" % "3.0.0.20120209-SNAPSHOT",
  "com.jme3" % "vecmath" % "3.0.0.20120209-SNAPSHOT",
  "com.jme3" % "xmlpull-xpp3" % "3.0.0.20120209-SNAPSHOT"
)
