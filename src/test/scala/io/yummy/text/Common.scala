package io.yummy.text

import io.yummy.text.model.IngestedText

object Common {
  val file         = scala.io.Source.fromResource("file.txt").mkString
  val text         = IngestedText("this this is is is file words")
  val pollutedText = IngestedText(",.th§.,.,.is.!\" §<>|%‹^th,+=is$ #is *&&is (is)` {}file[~ ?\'words.")
  val emptyText    = IngestedText("")
  val hugeText     = IngestedText(file)
}
