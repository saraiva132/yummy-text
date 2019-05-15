package io.yummy.text.digester

import cats.effect.Sync
import cats.implicits._
import io.yummy.text.model.{DigestedText, IngestedText}

case class TextDigester[F[_]]()(implicit F: Sync[F]) {

  private def retrieveWords(text: IngestedText): F[Seq[String]] = F.delay {
    text.value
      .filter(r => r.isLetter || r.isSpaceChar)
      .toLowerCase
      .split(" ")
      .filterNot(_.isEmpty)
  }

  private def countOccurrences(words: Seq[String]): F[Map[String, Int]] = F.delay {
    words
      .groupBy(identity)
      .map {
        case (key, value) =>
          (key, value.length)
      }
  }

  def digest(text: IngestedText): F[DigestedText] =
    for {
      words       <- retrieveWords(text)
      occurrences <- countOccurrences(words)
    } yield DigestedText(words.length, occurrences)

}
