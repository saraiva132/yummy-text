package io.yummy.text.validation

import io.yummy.text.Config.DigesterConfig
import io.yummy.text.model.IngestedText
import io.yummy.text.error._
import cats.syntax.all._
import cats.instances.int._

case class Validator(config: DigesterConfig) {

  private def validateIngestedText(value: IngestedText): ValidationResult[IngestedText] =
    for {
      v <- value.asRight
      _ <- Either.cond(value.value.length < config.fileMaxSize, v, IngestedFileTooLong)
      _ <- Either.cond(value.value.length =!= 0, v, IngestedFileIsEmpty)
    } yield v

  def validate(input: String): ValidationResult[IngestedText] =
    for {
      text     <- IngestedText(input).asRight
      response <- validateIngestedText(text)
    } yield response

}
