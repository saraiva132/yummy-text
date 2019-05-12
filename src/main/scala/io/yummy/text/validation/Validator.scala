package io.yummy.text.validation

import cats.data.EitherT
import cats.effect.IO
import io.yummy.text.model.IngestedText
import io.yummy.text.error._
import cats.implicits._
import fs2.Stream
import fs2.text._
import org.http4s.multipart.Multipart

case class Validator() {

  private val fileHeaderName = "file"

  def validateIngestedText(value: IngestedText): ValidationResult[IngestedText] =
    for {
      v <- value.asRight
      _ <- Either.cond(value.value.length < 10000000, v, IngestedFileTooLong)
      _ <- Either.cond(value.value.length =!= 0, v, IngestedFileIsEmpty)
    } yield v

  def digestTextStream(stream: Stream[IO, String]): ValidationResultT[IngestedText] = {
    val s = for {
      file     <- stream.compile.toVector
      text     <- IO.pure(IngestedText(file.mkString("")))
      response <- IO(validateIngestedText(text))
    } yield response

    EitherT(s)
  }

  def searchForFile(multiPart: Multipart[IO]): ValidationResultT[Stream[IO, String]] = EitherT.fromEither {
    multiPart.parts.find(_.name.map(_ === fileHeaderName).getOrElse(false)) match {
      case Some(part) =>
        Right(
          part.body
            .through(utf8Decode)
        )
      case None =>
        Left(TextFileNotFound)
    }
  }

  def validate(multiPart: Multipart[IO]): ValidationResultT[IngestedText] =
    for {
      stream    <- searchForFile(multiPart)
      text      <- digestTextStream(stream)
      validated <- EitherT.fromEither[IO](validateIngestedText(text))
    } yield validated

}
