package io.yummy.text.digester

import cats.effect.IO
import io.yummy.text.model.{DigestedText, IngestedText}
import cats.implicits._

object TextDigester {

  def digest(text: IngestedText): IO[DigestedText] = IO {
    val words = text.value.split(" ").filterNot(_ === "")

    val occurrences = words
      .groupBy(str => str)
      .map {
        case (key, value) =>
          (key, value.length)
      }

    DigestedText(words.length, occurrences)
  }

}
