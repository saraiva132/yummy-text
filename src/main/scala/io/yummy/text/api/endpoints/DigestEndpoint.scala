package io.yummy.text.api.endpoints

import cats.data.EitherT
import cats.effect.Sync
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
import io.yummy.text.model.DigestedText
import fs2.text._

import scala.util.control.NonFatal

case class DigestEndpoint[F[_]](config: DigesterConfig, validator: Validator, digester: TextDigester[F])(implicit F: Sync[F], L: Logger[F])
    extends Http4sDsl[F] {

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "digest" =>
        processMultiPartRequest(req)
    }

  private def processMultiPartRequest(req: Request[F]): F[Response[F]] =
    req.decode[Multipart[F]] { m =>
      val contentLength = req.contentLength.getOrElse(0L)

      val result = for {
        _             <- EitherT.cond[F](contentLength <= config.fileMaxSize, m, RequestTooLarge)
        text          <- retrieveTextFromRequest(m)
        validatedText <- EitherT.fromEither[F](validator.validate(text))
        digested      <- EitherT.liftF[F, Error, DigestedText](digester.digest(validatedText))
      } yield digested

      processResult(result)
    }

  private def processResult(result: ValidationResultT[F, DigestedText]): F[Response[F]] =
    result
      .fold(
        error =>
          L.warn(s"Failed to process file with error ${error.getMessage}. ") *>
          BadRequest(error.asJson),
        digested =>
          L.debug(s"Digested text file with result $digested.") *>
          Ok(digested.asJson)
      )
      .flatten
      .handleErrorWith { case NonFatal(_) => InternalServerError("Ops, something went wrong. Try again!") }

  private def retrieveTextFromRequest(multiPart: Multipart[F]): ValidationResultT[F, String] = {

    def findFileByName(part: Part[F]): Boolean =
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
