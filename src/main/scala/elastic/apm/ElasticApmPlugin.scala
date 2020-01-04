package elastic.apm

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptExtraDefines
import com.typesafe.sbt.packager.archetypes.scripts.{BashStartScriptPlugin, BatStartScriptPlugin}
import sbt.librarymanagement.DependencyFilter

object ElasticApmPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin && BashStartScriptPlugin && BatStartScriptPlugin

  object autoImport {
    lazy val elasticApmVersion = settingKey[String]("Elastic APM Agent version")
    lazy val elasticApmJavaAgent  = taskKey[File]("Elastic APM agent jar location")
    lazy val elasticApmServiceName = taskKey[String](
      "This is used to keep all the errors and transactions of your service together and is the primary filter in the Elastic APM user interface. The default value is the sbt project name.")
    lazy val elasticApmAgentApplicationPackages = taskKey[Seq[String]](
      "Used to determine whether a stack trace frame is an in-app frame or a library frame. Multiple packages can be set as a comma-separated list. Setting this option can also improve the startup time.")
    lazy val elasticApmAgentServerUrls = taskKey[Seq[URL]](
      "The URLs must be fully qualified, including protocol (http or https) and port.")
  }

  import autoImport._

  val ElasticApmConfig = config("elastic-apm-agent").hide

  override lazy val projectSettings = Seq(
    ivyConfigurations += ElasticApmConfig,
    elasticApmVersion := "1.6.1",
    elasticApmJavaAgent := findElasticApmJavaAgent(update.value),
    elasticApmServiceName := name.value,
    elasticApmAgentApplicationPackages := Seq("containers"),
    elasticApmAgentServerUrls := Seq(url("http://qa2-logsearch2.qa2.yodle.com:8200")),
    libraryDependencies += "co.elastic.apm" % "elastic-apm-agent" % elasticApmVersion.value % ElasticApmConfig,
    mappings in Universal += elasticApmJavaAgent.value -> "elasticApm/elastic-apm-agent.jar",
    bashScriptExtraDefines += """addJava "-javaagent:${app_home}/../elasticApm/elastic-apm-agent.jar"""",
    bashScriptExtraDefines += s"""addJava "-Delastic.apm.service_name=${elasticApmServiceName.value}"""",
    bashScriptExtraDefines += s"""addJava "-Delastic.apm.application_packages=${elasticApmAgentApplicationPackages.value.mkString(",")}"""",
    bashScriptExtraDefines += s"""addJava "-Delastic.apm.server_urls=${elasticApmAgentServerUrls.value.mkString(",")}""""
  )

  private[this] def findElasticApmJavaAgent(report: UpdateReport) = report.matching(elasticApmFilter).head

  private[this] val elasticApmFilter: DependencyFilter =
    configurationFilter("elastic-apm-agent") && artifactFilter(`type` = "jar")

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}