package io.yummy.text.api

import cats.implicits._
import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart.Multipart
import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.IngestedTextValidator

object Routes extends Http4sDsl[IO] {

  implicit val ingestedTextValidator = IngestedTextValidator()

  def apply()(implicit L: Logger[IO]): HttpRoutes[IO] =
      HttpRoutes.of[IO] {
        case req @ POST -> Root / "upload" => {
          req.decode[Multipart[IO]] { _ =>
            {
              L.info(s"${req.contentLength.get}") *>
              Ok()
            }
          }
      }
    }
}
