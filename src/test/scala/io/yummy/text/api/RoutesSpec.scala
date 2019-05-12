package io.yummy.text.api

import cats.effect.IO
import cats.implicits._
import io.yummy.text.Common._
import io.yummy.text.digester.TextDigester
import io.yummy.text.error._
import io.yummy.text.model.{DigestedText, IngestedText}
import io.yummy.text.validation.Validator
import org.http4s.{Header, Headers, Method, Request, Status, Uri}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import org.http4s.circe._
import org.http4s.multipart.{Multipart, Part}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import fs2.Stream

class RoutesSpec extends FlatSpec with Matchers with MockitoSugar {

  implicit val digestedTextEntityDecoder = jsonOf[IO, DigestedText]
  val textDigesterMock                   = mock[TextDigester]
  val validatorMock                      = mock[Validator]
  val uri                                = Uri.unsafeFromString("/upload")

  val routes = Routes(config.digester, validatorMock, textDigesterMock).routes

  "POST /upload" should "return the number of words and occurrences" in {

    when(textDigesterMock.digest(any[IngestedText]))
      .thenReturn(IO.pure(digested))

    when(validatorMock.validate(any[String]))
      .thenReturn(text.asRight)

    val request  = buildMultiPartRequest(text.value, uri)
    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.Ok
    response.as[DigestedText].unsafeRunSync() shouldBe digested

    verify(validatorMock).validate(text.value)
    verify(textDigesterMock).digest(text)
  }

  it should "not allow empty files" in {

    when(validatorMock.validate(any[String])).thenReturn(IngestedFileIsEmpty.asLeft)

    val request  = buildMultiPartRequest(emptyText.value, uri)
    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync().rmQuotes shouldBe IngestedFileIsEmpty.getMessage
  }

  it should "not allow files bigger than allowed size" in {

    when(validatorMock.validate(any[String])).thenReturn(IngestedFileTooLong.asLeft)

    val request  = buildMultiPartRequest(hugeText.value, uri)
    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync().rmQuotes shouldBe IngestedFileTooLong.getMessage
  }

  it should "inspect content length and reject if too large" in {

    val multiPart = buildMultiPart(text.value)
    val request = Request[IO](Method.POST, uri)
      .withHeaders(multiPart.headers)
      .withEntity(multiPart)
      .putHeaders(Header("content-length", "100000000"))

    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync().rmQuotes shouldBe RequestTooLarge.getMessage
  }

  it should "reject if not able to find file" in {

    val part      = Part[IO](Headers.of(Header("badHeader", "badContent")), Stream.empty)
    val multipart = Multipart[IO](Vector(part))
    val request = Request[IO](Method.POST, uri)
      .withHeaders(multipart.headers)
      .withEntity(multipart)

    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync().rmQuotes shouldBe TextFileNotFound(config.digester.partName).getMessage
  }

  "POST /upload" should "return internal server error if something unexpected occurs" in {

    when(validatorMock.validate(any[String]))
      .thenReturn(text.asRight)

    when(textDigesterMock.digest(any[IngestedText]))
      .thenReturn(IO.raiseError(new Exception))

    val request  = buildMultiPartRequest(text.value, uri)
    val response = routes.run(request).value.unsafeRunSync().get
    response.status shouldBe Status.InternalServerError
  }

  implicit class stringOps(str: String) {
    def rmQuotes = str.replace("\"", "")
  }

}
