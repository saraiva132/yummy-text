package io.yummy.text.api.endpoints

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

case class StatusEndpoint() extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "status" => Ok()
    }
}
