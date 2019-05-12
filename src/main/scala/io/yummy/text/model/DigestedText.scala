package io.yummy.text.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class DigestedText(wordCount: Int, wordOccurrences: Map[String, Int])

object DigestedText {

  implicit val digestedTextEncoder: Encoder[DigestedText] = deriveEncoder[DigestedText]
  implicit val digestedTextDecoder: Decoder[DigestedText] = deriveDecoder[DigestedText]

}
