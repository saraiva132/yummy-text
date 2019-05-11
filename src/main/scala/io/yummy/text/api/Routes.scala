package io.yummy.text.api

import cats.implicits._
import cats.effect.IO
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart.Multipart
import org.http4s.circe._
import io.chrisdavenport.log4cats.Logger
import fs2.Stream
import fs2.text._
import io.yummy.text.error._
import io.yummy.text.model.IngestedText
import io.yummy.text.validation._
import io.yummy.text.validation.IngestedTextValidator
import io.circe.syntax._
import io.yummy.text.digester.TextDigester

object Routes extends Http4sDsl[IO] {

  val fileHeaderName                 = "file"
  implicit val ingestedTextValidator = IngestedTextValidator()

  def apply()(implicit L: Logger[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "upload" => {
        req.decode[Multipart[IO]] { m =>
          searchForFile(m) match {
            case Right(stream) =>
              digestTextStream(stream)
            case Left(error)   =>
              L.info(s"Failed to process file with error ${error.getMessage}") *>
                BadRequest(error.asJson)
          }
        }
      }
    }

  private def searchForFile(multiPart: Multipart[IO]): Either[Error, Stream[IO, String]] =
    multiPart.parts.find(_.name.map(_ === fileHeaderName).getOrElse(false)) match {
      case Some(part) =>
        Right(
          part.body
            .through(utf8Decode)
        )
      case None =>
        Left(TextFileNotFound)
    }

  private def digestTextStream(stream: Stream[IO, String]): IO[Response[IO]] =
    for {
      file     <- stream.compile.toVector
      text     <- IO.pure(IngestedText(file.mkString("")))
      response <- withValidation[IO, IngestedText](text)(digestText)
    } yield response

  private def digestText(text: IngestedText): IO[Response[IO]] =
    for {
      digested <- TextDigester.digest(text)
      response <- Ok(digested.asJson)
    } yield response

}
