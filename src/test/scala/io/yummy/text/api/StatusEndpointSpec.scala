package io.yummy.text.api

import cats.effect.IO
import io.yummy.text.api.endpoints.StatusEndpoint
import org.http4s.{Method, Request, Status, Uri}
import org.scalatest.{FlatSpec, Matchers}

class StatusEndpointSpec extends FlatSpec with Matchers {

  val routes = StatusEndpoint[IO]().routes
  val uri    = Uri.unsafeFromString("/status")

  "GET /status" should "return OK for an healthy running service" in {
    val request = Request[IO](Method.GET, uri)
    routes.run(request).value.unsafeRunSync().get.status shouldBe Status.Ok
  }
}
