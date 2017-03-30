name := "FlightDelay"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"//Spark
libraryDependencies += "org.scalaj" % "scalaj-http_2.11" % "2.3.0"//Http request
libraryDependencies += "net.liftweb" %% "lift-json" % "2.6+"//Json.parse

libraryDependencies += "com.typesafe" % "config" % "1.2.1" //file io
libraryDependencies += "org.scalaj" % "scalaj-http_2.11" % "2.3.0"//file io