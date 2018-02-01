package com.mobcrush.composition.model

case class StartCompositionModel(outputStream: OutputStreamRequestModel, force: Option[Boolean] = None)

object StartCompositionModel {

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  private implicit val outputStreamRequestModelFormat: Format[OutputStreamRequestModel] = OutputStreamRequestModel.format

  private implicit val reads: Reads[StartCompositionModel] = (
    (JsPath \ "outputStream").read[OutputStreamRequestModel] and
    (JsPath \ "force").readNullable[Boolean]
  ) (StartCompositionModel.apply _)

  implicit val format: Format[StartCompositionModel] = Format(
    reads,
    Json.writes[StartCompositionModel]
  )
}
