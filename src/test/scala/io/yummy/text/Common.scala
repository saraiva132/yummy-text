package io.yummy.text

import cats.effect.IO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.yummy.text.model.{DigestedText, IngestedText}
import fs2.Chunk
import fs2.Stream
import org.http4s.{Headers, Method, Request, Uri}
import org.http4s.multipart.{Multipart, Part}
import org.http4s.headers._

object Common {

  implicit val logger = Slf4jLogger.getLoggerFromName[IO]("yummy-text-test")
  val config          = Config[IO].unsafeRunSync()
  val file            = scala.io.Source.fromResource("file.txt").mkString
  val text            = IngestedText("this this is is is file words")
  val pollutedText    = IngestedText(",.th§.,.,.is.!\" §<>|%‹^th,+=is$ #is *&&is (is)` {}file[~ ?\'words.")
  val emptyText       = IngestedText("")
  val hugeText        = IngestedText(file)
  val digested        = DigestedText(7, Map("this" -> 2, "is" -> 3, "file" -> 1, "words" -> 1))

  def buildMultiPart(text: String): Multipart[IO] = {
    val disposition = `Content-Disposition`("form-data", Map("name" -> "file"))
    val stream      = Stream.chunk(Chunk.bytes(text.getBytes()))
    val part        = Part[IO](Headers.of(disposition), stream)
    Multipart[IO](Vector(part))
  }

  def buildMultiPartRequest(text: String, uri: Uri): Request[IO] = {
    val multipart = buildMultiPart(text)
    Request[IO](Method.POST, uri)
      .withHeaders(multipart.headers)
      .withEntity(multipart)
  }
}
