import sbt._
import Keys._
import sbtbuildinfo.Plugin._
import sbtscalashim.Plugin._

object Common {
  val Xsd = config("xsd") extend(Compile)
  val Wsdl = config("wsdl") extend(Compile)
  val Soap11 = config("soap11") extend(Compile)
  val Soap12 = config("soap12") extend(Compile)

  val scalaxbCodegenSettings = Nil
  // val scalaxbCodegenSettings: Seq[Def.Setting[_]] = {
  //   import sbtscalaxb.Plugin._
  //   import ScalaxbKeys._
  //   def customScalaxbSettings(base: String): Seq[Project.Setting[_]] = Seq(
  //     sources <<= xsdSource map { xsd => Seq(xsd / (base + ".xsd")) },
  //     sourceManaged <<= baseDirectory / "src_managed",
  //     packageName := base,
  //     protocolFileName := base + "_xmlprotocol.scala",
  //     classPrefix := Some("X")
  //   )

  //   def soapSettings(base: String): Seq[Project.Setting[_]] = Seq(
  //     sources <<= xsdSource map { xsd => Seq(xsd / (base + ".xsd")) },
  //     sourceManaged <<= sourceDirectory(_ / "main" / "resources"),
  //     packageName := base,
  //     protocolFileName := base + "_xmlprotocol.scala",
  //     packageDir := false,
  //     generate <<= (generate) map { files =>
  //       val renamed = files map { file => new File(file.getParentFile, file.getName + ".template") }
  //       IO.move(files zip renamed)
  //       renamed
  //     }
  //   )

  //   inConfig(Xsd)(baseScalaxbSettings ++ inTask(scalaxb)(customScalaxbSettings("xmlschema"))) ++
  //   inConfig(Wsdl)(baseScalaxbSettings ++ inTask(scalaxb)(customScalaxbSettings("wsdl11"))) ++
  //   inConfig(Soap11)(baseScalaxbSettings ++ inTask(scalaxb)(soapSettings("soapenvelope11"))) ++
  //   inConfig(Soap12)(baseScalaxbSettings ++ inTask(scalaxb)(soapSettings("soapenvelope12")))
  // }

  val codegenSettings: Seq[Def.Setting[_]] = buildInfoSettings ++ scalaShimSettings ++ scalaxbCodegenSettings ++ Seq(
    unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "src_managed" ),
    buildInfoPackage := "scalaxb",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
      "defaultDispatchVersion" -> Dependencies.defaultDispatchVersion),
    sourceGenerators in Compile <+= buildInfo,
    sourceGenerators in Compile <+= scalaShim 
  )

  // val customLsSettings: Seq[Def.Setting[_]] = Nil
  val customLsSettings: Seq[Def.Setting[_]] = {
    import ls.Plugin.{LsKeys => lskeys}
    _root_.ls.Plugin.lsSettings ++ Seq(
      lskeys.tags in lskeys.lsync := Seq("xml", "soap", "wsdl", "code-generation"),
      (externalResolvers in lskeys.lsync) := Seq(Resolver.sonatypeRepo("public"))
    )
  }

  val sonatypeSettings: Seq[Def.Setting[_]] = Seq(
    pomExtra := (<scm>
        <url>git@github.com:eed3si9n/scalaxb.git</url>
        <connection>scm:git:git@github.com:eed3si9n/scalaxb.git</connection>
      </scm>
      <developers>
        <developer>
          <id>eed3si9n</id>
          <name>Eugene Yokota</name>
          <url>http://eed3si9n.com</url>
        </developer>
      </developers>),
    publishArtifact in Test := false,
    //local
//    publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository/"))),
    // publish to local nexus with credentials
    publishTo := Some("Sonatype Nexus Repository Manager" at "http://IPNEXUS:8081/nexus/content/repositories/thirdparty"),
    credentials += Credentials(Path.userHome / ".m2" / ".credentials"),
    publishMavenStyle := true,
    pomIncludeRepository := { x => false }
  )
}
