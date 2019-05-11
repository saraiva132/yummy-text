package io.yummy.text

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.yummy.text.api.Routes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

// format: off
object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] =
    for {
      routes <- IO(Routes())
      _      <- BlazeServerBuilder[IO]
        .bindHttp(port = 8080, host = "0.0.0.0")
        .withHttpApp(routes.orNotFound)
        .serve
        .compile
        .drain
    } yield ExitCode.Success
}
