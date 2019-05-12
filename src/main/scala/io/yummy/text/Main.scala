package io.yummy.text

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.yummy.text.api.Routes
import io.yummy.text.digester.TextDigester
import io.yummy.text.validation.Validator
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import cats.implicits._

// format: off
object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLoggerFromName[IO]("yummy-text")

  def run(args: List[String]): IO[ExitCode] =
    for {
      config     <- Config.apply
      digester   <- IO.pure(TextDigester())
      validator  <- IO.pure(Validator(config.digester))
      api        <- IO(Routes(config.digester, validator, digester))
      result     <- BlazeServerBuilder[IO]
        .bindHttp(port = config.server.port, host = config.server.host)
        .withHttpApp(api.routes.orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    } yield result
}
