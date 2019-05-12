package io.yummy.text.digester

import cats.effect.IO
import io.yummy.text.model.{DigestedText, IngestedText}

object TextDigester {

  def digest(text: IngestedText): IO[DigestedText] = IO {
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
