package io.yummy.text

import cats.data.Validated.{Invalid, Valid}
import cats.data.{ValidatedNec}
import cats.effect.{Sync}
import org.http4s.{Response, Status}
import io.yummy.text.error._
import io.circe.syntax._
import org.http4s.circe._

package validation {

  trait Validator[T] {
    def validate(value: T): ValidationResult[T]
  }

}

package object validation {

  type ValidationResult[A] = ValidatedNec[Error, A]

  def withValidation[F[_]: Sync, T: Validator](value: T)(f: T => F[Response[F]]): F[Response[F]] =
    implicitly[Validator[T]].validate(value) match {
      case Valid(v) ⇒
        f(v)
      case Invalid(e) ⇒
        Sync[F].delay(
          Response[F](Status.BadRequest)
            .withEntity(e.toNonEmptyList.toList.asJson)
        )
    }
}
