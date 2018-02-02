package com.mobcrush.composition.controller

import com.google.inject.{Inject, Singleton}
import com.mobcrush.composition.model.{CompositionResponseModel, CreateCompositionModel, StartCompositionModel}
import com.mobcrush.composition.service.CompositionServiceImpl
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

@Singleton
class CompositionController @Inject()(cc: ControllerComponents, config: Configuration, service: CompositionServiceImpl) extends AbstractController(cc) {

  private val logger: Logger = Logger(this.getClass)

  implicit val requestModelFormat: Format[CreateCompositionModel] = CreateCompositionModel.format
  implicit val responseModelFormat: Format[CompositionResponseModel] = CompositionResponseModel.format
  implicit val startCompositionFormat: Format[StartCompositionModel] = StartCompositionModel.format

  def create() = Action { implicit request: Request[AnyContent] =>

    val jsResult: JsResult[CreateCompositionModel] = Json.fromJson[CreateCompositionModel](request.body.asJson.get)

    if (jsResult.isError) {
      logger.error("Cannot parse request body: " + request.body.asText.get)
      BadRequest("Cannot parse request body")
    } else {
      val requestModel: CreateCompositionModel = jsResult.get
      val responseModel: CompositionResponseModel = service.create(requestModel)
      Ok(Json.toJson(responseModel))
    }
  }

  def attach(compositionKey: String) = Action { implicit request: Request[AnyContent] =>

    val responseModel = service.attach(compositionKey)
    if (responseModel == null) {
      BadRequest
    } else {
      Ok(Json.toJson(responseModel))
    }
  }

  def start(compositionKey: String) = Action { implicit request: Request[AnyContent] =>

    val jsResult: JsResult[StartCompositionModel] = request.body.asJson.get.validate[StartCompositionModel]

    jsResult match {
      case s: JsSuccess[StartCompositionModel] => {
        val requestModel: StartCompositionModel = s.get
        service.start(compositionKey, requestModel)

        Ok
      }
      case e: JsError => {
//        formatErrorResponseModel(e)

        BadRequest("Cannot parse request body")
      }
    }

    /*if (jsResult.isError) {
      logger.error("Cannot parse request body: " + request.body.asText.get)
      BadRequest("Cannot parse request body")
    } else {

      val requestModel: StartCompositionModel = jsResult.get
      val responseModel = service.start(compositionKey, requestModel)

      Ok
//      if (responseModel == null) {
//        BadRequest
//      } else {
//        Ok(Json.toJson(responseModel))
//      }
    }*/

  }

  def stop(compositionKey: String) = Action { implicit request: Request[AnyContent] =>
    val result = service.stop(compositionKey)

    if (result) {
      Ok
    } else {
      BadRequest
    }
  }

  private def formatErrorResponseModel(jsError: JsError): String = {
    jsError.errors.map {
      case (path: JsPath, errors: Seq[JsonValidationError]) => {
        errors.foreach(error => {
          error.message
        })
      }
    }
    ""
//    logger.error("Cannot parse request body: " + request.body.asText.get)
  }
}
