package io.yummy.text.validation

import cats.data.EitherT
import cats.effect.IO
import io.yummy.text.model.IngestedText
import io.yummy.text.error._
import cats.implicits._
import fs2.Stream
import io.yummy.text.Config.DigesterConfig

case class Validator(config: DigesterConfig) {

  private def validateIngestedText(value: IngestedText): ValidationResult[IngestedText] =
    for {
      v <- value.asRight
      _ <- Either.cond(value.value.length < config.fileMaxSize, v, IngestedFileTooLong)
      _ <- Either.cond(value.value.length =!= 0, v, IngestedFileIsEmpty)
    } yield v

  def validate(stream: Stream[IO, String]): ValidationResultT[IngestedText] = {
    val s = for {
      file     <- stream.compile.toVector
      text     <- IO(IngestedText(file.mkString("")))
      response <- IO(validateIngestedText(text))
    } yield response

    EitherT(s)
  }

}
