package io.yummy.text.validation

import cats.data.Validated.{Invalid, Valid}
import io.yummy.text.model.IngestedText
import cats.data.NonEmptyChain
import io.yummy.text.error._
import cats.implicits._

case class IngestedTextValidator() extends Validator[IngestedText] {

  override def validate(value: IngestedText): ValidationResult[IngestedText] =
    if (value.value.length > 10000000)
      Invalid(NonEmptyChain.one(IngestedFileTooLong))
    else if (value.value.length === 0)
      Invalid(NonEmptyChain.one(IngestedFileIsEmpty))
    else
      Valid(value)
}
