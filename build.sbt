scalaJSSettings

name := "mg-client"

organization := "com.github.benhutchison"

scalaVersion := "2.11.2"

libraryDependencies += "com.github.benhutchison" %%% "mg-domain" % "0.1"

libraryDependencies += "com.github.benhutchison" %%% "prickle" % "1.0.3"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"

libraryDependencies += "com.scalatags" %%% "scalatags" % "0.4.2"

libraryDependencies += "com.lihaoyi" %% "autowire" % "0.2.3"