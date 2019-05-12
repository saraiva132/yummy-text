package io.yummy.text.api

import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart.Multipart
import org.http4s.circe._
import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.Validator
import io.circe.syntax._
import io.yummy.text.digester.TextDigester
import io.yummy.text.model.DigestedText
import io.yummy.text.error._

object Routes extends Http4sDsl[IO] {

  def apply(validator: Validator)(implicit L: Logger[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "upload" => {
        req.decode[Multipart[IO]] { m =>
          val contentLength = req.contentLength.getOrElse(0)

          val d = for {
            _         <- EitherT.cond(contentLength < 10000000, (), RequestTooLong)
            validated <- validator.validate(m)
            digested  <- EitherT.liftF[IO, Error, DigestedText](TextDigester.digest(validated))
          } yield digested

          d.value.flatMap {
            case Right(digested) =>
              Ok(digested.asJson)
            case Left(error) =>
              L.info(s"Failed to process file with error ${error.getMessage}") *> BadRequest(error.asJson)
          }
        }
      }
    }
}
