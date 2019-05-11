package io.yummy.text.error

import io.circe.Encoder

sealed abstract class Error(message: Option[String]) extends Exception(message.getOrElse(""))

object Error {
  implicit val errorEncoder: Encoder[Error] = Encoder.encodeString.contramap(_.getMessage)
}

final case object IngestedFileIsEmpty extends Error(Some("Ingested file should not be empty."))
final case object IngestedFileTooLong extends Error(Some("Ingested file length is too big. Maximum size is 10 mb."))
