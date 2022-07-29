// feel free to customize below values.

ThisBuild / scalaVersion := "2.11.12" // 2.12.10 also possible
ThisBuild / name := "$name$"
ThisBuild / organization := "$organization$"
ThisBuild / version := "$version$"

resolvers += ("uuverifiers" at "http://logicrunch.research.it.uu.se/maven/").withAllowInsecureProtocol(true)

// eldarica version can be changed to a stable one below
libraryDependencies += "uuverifiers" %% "eldarica" % "nightly-SNAPSHOT"

