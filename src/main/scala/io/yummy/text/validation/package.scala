package io.yummy.text

import cats.data.EitherT
import cats.effect.IO
import io.yummy.text.error._

package object validation {
  type ValidationResult[A] = Either[Error, A]
  type ValidationResultT[A] = EitherT[IO, Error, A]

}
