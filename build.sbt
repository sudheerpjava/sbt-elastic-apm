name := "sbt-apm"

version := "0.1"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/sudheerpjava/sbt-elastic-apm"))

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.5.2")

enablePlugins(SbtPlugin)

bintrayPackageLabels := Seq("sbt", "plugin")

bintrayVcsUrl := Some("https://github.com/sudheerpjava/sbt-elastic-apm")

bintrayReleaseOnPublish := false
