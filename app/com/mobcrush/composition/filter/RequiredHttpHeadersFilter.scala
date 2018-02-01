package com.mobcrush.composition.filter

import java.util.concurrent.CompletionStage
import java.util.function

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.mvc._

import scala.concurrent.ExecutionContext

class RequiredHttpHeadersFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {


  override def apply(next: function.Function[RequestHeader, CompletionStage[Result]],
                     rh: RequestHeader
                    ): CompletionStage[Result] = {

    ne(rh)
  }
}
