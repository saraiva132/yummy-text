package io.yummy.text.digester

import cats.effect.Sync
import io.yummy.text.model.{DigestedText, IngestedText}

case class TextDigester[F[_]]()(implicit F: Sync[F]) {

  def digest(text: IngestedText): F[DigestedText] = F.delay {
    val words = text.value
      .filter(r => r.isLetter || r.isSpaceChar)
      .toLowerCase
      .split(" ")
      .filterNot(_.isEmpty)

    val occurrences = words
      .groupBy(identity)
      .map {
        case (key, value) =>
          (key, value.length)
      }

    DigestedText(words.length, occurrences)
  }

}
