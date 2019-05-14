package io.yummy.text.api

import cats.effect.Sync
import cats.syntax.semigroupk._
import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.Validator
import io.yummy.text.digester.TextDigester
import io.yummy.text.Config.DigesterConfig
import io.yummy.text.api.endpoints.{DigestEndpoint, StatusEndpoint}

case class Api[F[_]](config: DigesterConfig, validator: Validator, digester: TextDigester[F])(implicit F: Sync[F], L: Logger[F]) {

  val digestEndpoint = DigestEndpoint[F](config, validator, digester)
  val statusEndpoint = StatusEndpoint[F]()

  val routes = statusEndpoint.routes <+> digestEndpoint.routes

}
