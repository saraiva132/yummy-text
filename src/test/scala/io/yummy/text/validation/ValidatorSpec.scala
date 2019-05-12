package io.yummy.text.validation

import cats.effect.IO
import io.yummy.text.error.{IngestedFileIsEmpty, IngestedFileTooLong}
import org.scalatest.{FlatSpec, Matchers}
import org.http4s.dsl.Http4sDsl
import io.yummy.text.Common._
import fs2.Stream

class ValidatorSpec extends FlatSpec with Matchers with Http4sDsl[IO] {
  val validator = Validator(config.digester)

  it should "validate correct text" in {
    val stream = Stream.eval(IO.pure(text.value))
    validator.validate(stream).value.unsafeRunSync().right.get shouldBe text
  }

  it should "validate return error if file is empty" in {
    val stream = Stream.eval(IO.pure(emptyText.value))
    validator.validate(stream).value.unsafeRunSync().left.get shouldBe IngestedFileIsEmpty
  }

  it should "validate return error if file is too big" in {
    val stream = Stream.eval(IO.pure(hugeText.value))
    validator.validate(stream).value.unsafeRunSync().left.get shouldBe IngestedFileTooLong
  }

}
