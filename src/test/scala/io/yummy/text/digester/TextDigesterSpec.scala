package io.yummy.text.digester

import cats.effect.IO
import org.scalatest.{FlatSpec, Matchers}
import io.yummy.text.Common._

class TextDigesterSpec extends FlatSpec with Matchers {

  val digester = TextDigester[IO]()

  it should "count number of words and occurrences" in {
    digester.digest(text).unsafeRunSync() shouldBe digested
  }

  it should "be able to filter out illegal characters and digits" in {
    digester.digest(pollutedText).unsafeRunSync() shouldBe digested
  }

}
