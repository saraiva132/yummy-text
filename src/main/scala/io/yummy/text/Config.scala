package io.yummy.text

import Config._
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import pureconfig.generic.auto._
import pureconfig.module.catseffect.loadConfigF
import scala.collection.JavaConverters._
import scala.util.control.NonFatal

case class Config(server: ServerConfig, digester: DigesterConfig)

object Config {

  case class DigesterConfig(partName: String, fileMaxSize: Long)
  case class ServerConfig(host: String, port: Int)

  private val basePath = "io.yummy.text"

  private def dumpEnv[F[_]](implicit F: Sync[F], L: Logger[F]): PartialFunction[Throwable, F[Unit]] = {
    case NonFatal(ex) ⇒
      for {
        e   ← F.delay(System.getenv().asScala.map(v ⇒ s"${v._1} = ${v._2}").mkString("\n", "\n", ""))
        _   ← L.error(s"Loading configuration failed with the following environment variables: $e")
        err ← F.raiseError[Unit](ex)
      } yield err
  }

  def apply[F[_]](implicit F: Sync[F], L: Logger[F]): F[Config] =
    (for {
      c ← loadConfigF[F, Config](basePath)
      _ ← L.info(s"Successfully loaded configuration to: $c")
    } yield c).onError(dumpEnv)

}
