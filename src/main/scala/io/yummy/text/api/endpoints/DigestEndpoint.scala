package io.yummy.text.api.endpoints

import cats.data.EitherT
import cats.effect.IO
import cats.instances.string._
import cats.syntax.all._

import org.http4s.multipart.{Multipart, Part}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

import io.chrisdavenport.log4cats.Logger
import io.yummy.text.validation.{ValidationResultT, Validator}
import io.circe.syntax._
import io.yummy.text.digester.TextDigester
import io.yummy.text.error._
import io.yummy.text.Config.DigesterConfig
import io.yummy.text.model.{DigestedText}

import fs2.text._

case class DigestEndpoint(config: DigesterConfig, validator: Validator, digester: TextDigester)(implicit L: Logger[IO]) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "digest" =>
        processMultiPartRequest(req)
    }

  private def processMultiPartRequest(req: Request[IO]): IO[Response[IO]] =
    req.decode[Multipart[IO]] { m =>
      val contentLength = req.contentLength.getOrElse(0L)

      val result = for {
        _             <- EitherT.cond[IO](contentLength <= config.fileMaxSize, m, RequestTooLarge)
        text          <- retrieveTextFromRequest(m)
        validatedText <- EitherT.fromEither[IO](validator.validate(text))
        digested      <- EitherT.liftF[IO, Error, DigestedText](digester.digest(validatedText))
      } yield digested

      processResult(result)
    }

  private def processResult(result: ValidationResultT[DigestedText]): IO[Response[IO]] =
    result.value
      .flatMap {
        case Right(digested) =>
          L.debug(s"Digested text file with result $digested.") *>
          Ok(digested.asJson)
        case Left(error) =>
          L.warn(s"Failed to process file with error ${error.getMessage}. ") *>
          BadRequest(error.asJson)
      }
      .handleErrorWith(_ => InternalServerError("Ops, something went wrong. Try again!"))

  private def retrieveTextFromRequest(multiPart: Multipart[IO]): ValidationResultT[String] = {

    def findFileByName(part: Part[IO]): Boolean =
      part.name.map(_ === config.partName).getOrElse(false)

    multiPart.parts.find(findFileByName) match {
      case Some(part) =>
        EitherT.liftF(
          part.body
            .through(utf8Decode)
            .compile
            .toVector
            .map(_.mkString(""))
        )

      case None =>
        EitherT.leftT(TextFileNotFound(config.partName))
    }
  }
}
