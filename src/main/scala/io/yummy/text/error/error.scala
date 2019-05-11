package io.yummy.text.error

import io.circe.Encoder

sealed abstract class Error(message: String) extends Exception(message)

object Error {
  implicit val errorEncoder: Encoder[Error] = Encoder.encodeString.contramap(_.getMessage)
}

final case object IngestedFileIsEmpty extends Error("Ingested file should not be empty.")
final case object IngestedFileTooLong extends Error("Ingested file length is too big. Maximum size is 10 mb.")
final case object TextFileNotFound extends Error("Text file was not found. Please use multi-part header named file.")