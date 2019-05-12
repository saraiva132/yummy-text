package io.yummy.text.validation

import cats.effect.IO
import io.yummy.text.error.{IngestedFileIsEmpty, IngestedFileTooLong}
import io.yummy.text.model.IngestedText
import org.scalatest.{FlatSpec, Matchers}
import org.http4s.Status
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.yummy.text.Common._

class IngestedTextValidatorSpec extends FlatSpec with Matchers with Http4sDsl[IO] {

  implicit val stringListDecoder = jsonOf[IO, List[String]]
  val validator                  = Validator()

  it should "validate correct text" in {
    withValidation[IO, IngestedText](text)(_ => Ok()).unsafeRunSync().status shouldBe Status.Ok
  }

  it should "validate return error if file is empty" in {
    val result = withValidation[IO, IngestedText](emptyText)(_ => Ok()).unsafeRunSync()

    result.status shouldBe Status.BadRequest
    result.as[List[String]].unsafeRunSync() shouldBe List(IngestedFileIsEmpty.getMessage)
  }

  it should "validate return error if file is too big" in {
    val result = withValidation[IO, IngestedText](hugeText)(_ => Ok()).unsafeRunSync()

    result.status shouldBe Status.BadRequest
    result.as[List[String]].unsafeRunSync() shouldBe List(IngestedFileTooLong.getMessage)
  }

}
