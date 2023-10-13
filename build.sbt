import com.typesafe.sbt.SbtNativePackager.autoImport.NativePackagerHelper.*

ThisProject / maintainer := "toomuch.js@gmail.com"

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "selva"
ThisBuild / organizationName := "Selva"

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root = (project in file("."))
  .settings(
    name          := "rinha",
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"                   % "2.0.18",
      "dev.zio"       %% "zio-macros"            % "2.0.18",
      "dev.zio"       %% "zio-http"              % "3.0.0-RC2",
      "dev.zio"       %% "zio-json"              % "0.6.2",
      "dev.zio"       %% "zio-config"            % "4.0.0-RC16",
      "dev.zio"       %% "zio-config-magnolia"   % "4.0.0-RC16",
      "dev.zio"       %% "zio-config-typesafe"   % "4.0.0-RC16",
      "dev.zio"       %% "zio-config-refined"    % "4.0.0-RC16",
      "dev.zio"       %% "zio-logging"           % "2.1.14",
      "dev.zio"       %% "zio-logging-slf4j2"    % "2.1.14",
      "dev.zio"       %% "zio-redis"             % "0.2.0",
      "dev.zio"       %% "zio-schema"            % "0.4.9",
      "dev.zio"       %% "zio-schema-protobuf"   % "0.4.9",
      "dev.zio"       %% "zio-schema-derivation" % "0.4.9",
      "ch.qos.logback" % "logback-classic"       % "1.4.7",
      "io.getquill"   %% "quill-jdbc-zio"        % "4.8.0",
      "org.postgresql" % "postgresql"            % "42.3.1",
      "com.zaxxer"     % "HikariCP"              % "5.0.1",
      "org.flywaydb"   % "flyway-core"           % "9.16.0",
      "io.scalaland"  %% "chimney"               % "0.8.0",
      "org.scala-lang" % "scala-reflect"         % scalaVersion.value % "provided",
      "dev.zio"       %% "zio-test"              % "2.0.18"           % Test,
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    scalacOptions := List("-Wunused:imports", "-Ymacro-annotations"),
  )
  .enablePlugins(JavaAppPackaging)

Universal / topLevelDirectory := None
Universal / mappings ++= directory("src/main/resources")

Global / excludeLintKeys ++= Set(maintainer, topLevelDirectory)
