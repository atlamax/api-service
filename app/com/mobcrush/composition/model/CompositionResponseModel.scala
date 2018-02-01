package com.mobcrush.composition.model

case class CompositionResponseModel(streamURL: String)

object CompositionResponseModel {

  import play.api.libs.json.{Format, Json}

  implicit val format: Format[CompositionResponseModel] = Json.format[CompositionResponseModel]
}
