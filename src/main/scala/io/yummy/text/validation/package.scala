package io.yummy.text

import cats.data.EitherT
import io.yummy.text.error._

package object validation {
  type ValidationResult[A] = Either[Error, A]
  type ValidationResultT[F[_], A] = EitherT[F, Error, A]
}
