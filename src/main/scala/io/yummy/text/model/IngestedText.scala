package io.yummy.text.model

import io.circe.Encoder
import io.circe.generic.semiauto._

case class IngestedText(value: String)

object IngestedText {

  implicit val ingestedTextEncoder: Encoder[IngestedText] = deriveEncoder[IngestedText]

}
