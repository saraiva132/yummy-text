package io.yummy.text.api

import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.{ValidationResultT, Validator}
import io.circe.syntax._
import io.yummy.text.digester.TextDigester
import io.yummy.text.model.DigestedText
import io.yummy.text.error._
import fs2.text._
import fs2.Stream
import io.yummy.text.Config.DigesterConfig
import org.http4s.multipart.{Multipart, Part}

case class Routes(config: DigesterConfig, validator: Validator, digester: TextDigester)(implicit L: Logger[IO]) extends Http4sDsl[IO] {

  def routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "upload" => {
        req.decode[Multipart[IO]] { m =>
          val contentLength = req.contentLength.getOrElse(0L)

          val d = for {
            _        <- EitherT.cond[IO](contentLength <= config.fileMaxSize, m, RequestTooLarge)
            file     <- searchForFile(m)
            text     <- validator.validate(file)
            digested <- EitherT.liftF[IO, Error, DigestedText](digester.digest(text))
          } yield digested

          d.value.flatMap {
            case Right(digested) =>
              L.debug(s"Digested text file with result $digested.") *> Ok(digested.asJson)
            case Left(error) =>
              L.warn(s"Failed to process file with error ${error.getMessage}. ") *> BadRequest(error.asJson)
          }
        }
      }
    }

  def searchForFile(multiPart: Multipart[IO]): ValidationResultT[Stream[IO, String]] = {

    def findFileByName(part: Part[IO]): Boolean =
      part.name.map(_ === config.partName).getOrElse(false)

    EitherT.fromEither {
      multiPart.parts.find(findFileByName) match {
        case Some(part) =>
          Right(
            part.body
              .through(utf8Decode)
          )
        case None =>
          Left(TextFileNotFound(config.partName))
      }
    }
  }

}
