package io.yummy.text

import Config._
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import pureconfig.generic.auto._
import pureconfig.module.catseffect.loadConfigF

case class Config(server: ServerConfig, digester: DigesterConfig)

object Config {

  case class DigesterConfig(partName: String, fileMaxSize: Long)
  case class ServerConfig(host: String, port: Int)

  private val basePath = "io.yummy.text"

  def apply[F[_]](implicit F: Sync[F], L: Logger[F]): F[Config] =
    for {
      c ← loadConfigF[F, Config](basePath)
      _ ← L.info(s"Successfully loaded configuration to: $c")
    } yield c

}
