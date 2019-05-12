package io.yummy.text.validation

import cats.data.EitherT
import cats.effect.IO
import io.yummy.text.model.IngestedText
import io.yummy.text.error._
import cats.implicits._
import io.yummy.text.Config.DigesterConfig

case class Validator(config: DigesterConfig) {

  private def validateIngestedText(value: IngestedText): ValidationResult[IngestedText] =
    for {
      v <- value.asRight
      _ <- Either.cond(value.value.length < config.fileMaxSize, v, IngestedFileTooLong)
      _ <- Either.cond(value.value.length =!= 0, v, IngestedFileIsEmpty)
    } yield v

  def validate(input: String): ValidationResultT[IngestedText] = {
    val s = for {
      text     <- IO.pure(IngestedText(input))
      response <- IO(validateIngestedText(text))
    } yield response

    EitherT(s)
  }

}
