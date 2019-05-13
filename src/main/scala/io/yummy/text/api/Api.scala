package io.yummy.text.api

import cats.effect.IO
import cats.syntax.semigroupk._
import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.{Validator}
import io.yummy.text.digester.TextDigester
import io.yummy.text.Config.DigesterConfig
import io.yummy.text.api.endpoints.{DigestEndpoint, StatusEndpoint}

case class Api(config: DigesterConfig, validator: Validator, digester: TextDigester)(implicit L: Logger[IO]) {

  val digestEndpoint = DigestEndpoint(config, validator, digester)
  val statusEndpoint = StatusEndpoint()

  val routes = statusEndpoint.routes <+> digestEndpoint.routes

}
