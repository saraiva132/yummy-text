package io.yummy.text.api.endpoints

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

case class StatusEndpoint[F[_]]()(implicit F : Sync[F]) extends Http4sDsl[F] {

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "status" => Ok()
    }
}
