package com.mobcrush.composition.model

case class OutputStreamRequestModel(url: String, var width: Int = 0, var height: Int = 0)

object OutputStreamRequestModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._
  import play.api.libs.json._

  private implicit val reads: Reads[OutputStreamRequestModel] = (
    (JsPath \ "url").read[String] (minLength[String](1)) and
    (JsPath \ "width").read[Int] and
    (JsPath \ "height").read[Int]
  )(OutputStreamRequestModel.apply _)

  implicit val format: Format[OutputStreamRequestModel] = Format(
    reads,
    Json.writes[OutputStreamRequestModel]
  )
}
