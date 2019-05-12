package io.yummy.text.validation

import cats.effect.IO
import io.yummy.text.error.{IngestedFileIsEmpty, IngestedFileTooLong}
import org.scalatest.{FlatSpec, Matchers}
import org.http4s.dsl.Http4sDsl
import io.yummy.text.Common._

class ValidatorSpec extends FlatSpec with Matchers with Http4sDsl[IO] {
  val validator = Validator(config.digester)

  it should "validate correct text" in {
    validator.validate(text.value).right.get shouldBe text
  }

  it should "validate return error if file is empty" in {
    validator.validate(emptyText.value).left.get shouldBe IngestedFileIsEmpty
  }

  it should "validate return error if file is too big" in {
    validator.validate(hugeText.value).left.get shouldBe IngestedFileTooLong
  }

}
