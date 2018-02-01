package com.mobcrush.composition.model

case class CreateCompositionModel(compositionKey: String)

object CreateCompositionModel {

  import play.api.libs.json.{Format, Json}

  implicit val format: Format[CreateCompositionModel] = Json.format[CreateCompositionModel]
}