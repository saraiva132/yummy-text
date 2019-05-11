package io.yummy.text.digester

import org.scalatest.{FlatSpec, Matchers}
import io.yummy.text.Common._
import io.yummy.text.model.DigestedText

class TextDigesterSpec extends FlatSpec with Matchers {

  it should "count number of words and ocurrences" in {
    TextDigester.digest(text).unsafeRunSync() shouldBe DigestedText(7, Map("this" -> 2, "is" -> 3, "file" -> 1, "words" -> 1))
  }

  it should "be able to filter out illegal characters and digits" in {
    TextDigester.digest(pollutedText).unsafeRunSync() shouldBe DigestedText(7, Map("this" -> 2, "is" -> 3, "file" -> 1, "words" -> 1))
  }

}
