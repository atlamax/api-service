package com.mobcrush.composition.service

import java.util.concurrent.TimeUnit
import javax.annotation.{Nonnull, Nullable}

import com.google.inject.{Inject, Singleton}
import com.mobcrush.composition.dao.InMemoryCompositionDAO
import com.mobcrush.composition.factory.{InputStreamNameGenerator, OutputStreamNameGenerator, WowzaURLFactory}
import com.mobcrush.composition.model.{Composition, CompositionResponseModel, CreateCompositionModel, WowzaStartStreamRequestModel}
import play.api.Logger
import play.api.libs.json.{Format, Json}
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Success}

@Singleton
class CompositionServiceImpl @Inject()(
                                        dao: InMemoryCompositionDAO,
                                        inputStreamNameGenerator: InputStreamNameGenerator,
                                        outputStreamNameGenerator: OutputStreamNameGenerator,
                                        wowzaURLFactory: WowzaURLFactory,
                                        ws: WSClient
                                      ) extends CompositionService {

  private val logger: Logger = Logger(this.getClass)

  implicit val wowzaRequestFormat: Format[WowzaStartStreamRequestModel] = Json.format[WowzaStartStreamRequestModel]

  @Nonnull
  override def create(model: CreateCompositionModel): CompositionResponseModel = {
    val compositionKey = model.compositionKey
    logger.info(s"Going to create composition with key: '$compositionKey'")
    if (dao.get(compositionKey) != null) {
      val composition = dao.get(compositionKey)
      logger.info(s"Composition for key '$compositionKey' already exists, return URL: '${composition.slaveStreamURL}'")

      return CompositionResponseModel(composition.slaveStreamURL)
    }

    val slaveStreamURL: String = wowzaURLFactory.createRTMP(
      inputStreamNameGenerator.generate()
    )
    dao.add(
      compositionKey,
      Composition(slaveStreamURL)
    )
    logger.info(s"Create composition for key '$compositionKey', return URL: '$slaveStreamURL'")

    CompositionResponseModel(slaveStreamURL)
  }

  @Nullable
  override def attach(compositionKey: String): CompositionResponseModel = {
    logger.info(s"Going to attach to composition with key: '$compositionKey'")
    val composition = dao.get(compositionKey)
    if (composition == null) {
      logger.error(s"Composition for key '$compositionKey' not exists")
      return null
    }

    if (composition.masterStreamURL.nonEmpty) {
      logger.warn(s"Already atached to composition with key '$compositionKey', return URL: '${composition.masterStreamURL}'")
      return CompositionResponseModel(composition.masterStreamURL)
    }

    val masterStreamURL: String = wowzaURLFactory.createRTMP(
      inputStreamNameGenerator.generate()
    )
    composition.masterStreamURL = masterStreamURL
    dao.update(compositionKey, composition)
    logger.info(s"Attach to composition with key '$compositionKey', return URL: '$masterStreamURL'")

    CompositionResponseModel(masterStreamURL)
  }

  @Nullable
  override def start(compositionKey: String): CompositionResponseModel = {
    logger.info(s"Going to start composition with key: '$compositionKey'")
    val composition = dao.get(compositionKey)

    if (composition == null || composition.masterStreamURL.isEmpty) {
      logger.error(s"Composition with key '$compositionKey' do not exists, or it has no attached stream")
      return null
    }

    if (composition.targetStreamURL.nonEmpty) {
      logger.warn(s"Composition with key '$compositionKey' already started, return URL: '${composition.targetStreamURL}'")
      return CompositionResponseModel(composition.targetStreamURL)
    }

    val targetStreamURL: String = wowzaURLFactory.createRTMP(
      outputStreamNameGenerator.generate()
    )

    val wowzaRequestModel: WowzaStartStreamRequestModel = WowzaStartStreamRequestModel(
      composition.masterStreamURL,
      composition.slaveStreamURL,
      targetStreamURL
    )

    val request: WSRequest = ws
      .url(wowzaURLFactory.createStartCompositionHTTP())
      .withRequestTimeout(new FiniteDuration(5, TimeUnit.SECONDS))

    val responseFuture = request.post(Json.toJson(wowzaRequestModel))
    val responseTry = Await.ready(responseFuture, Duration.Inf).value.get
    responseTry match {
      case Success(t) => {
        if (t.status != 200) {
          logger.error(s"Response from Wowza has status: '${t.status}', body: '${t.body}'")
          return null
        }

        logger.info(s"Successful request to start composition finished with response status from Wowza: '${t.status}', body: '${t.body}'")
        composition.targetStreamURL = targetStreamURL
        dao.update(compositionKey, composition)

        logger.info(s"Started composition with key: '$compositionKey', return URL: '$targetStreamURL'")
        CompositionResponseModel(targetStreamURL)
      }

      case Failure(e) => {
        logger.error(s"Request to Wowza failed with message: ${e.getMessage}", e)
        null
      }
    }
  }

  override def stop(compositionKey: String): Boolean = {
    logger.info(s"Going to stop composition with key: '$compositionKey'")
    val composition = dao.get(compositionKey)
    if (composition == null) {
      logger.error(s"Composition with key: '$compositionKey' not exists")
      return false
    }

    val request: WSRequest = ws
      .url(wowzaURLFactory.createStopCompositionHTTP())
      .withQueryStringParameters(
        ("streamName", parseStreamName(composition.targetStreamURL))
      )
      .withRequestTimeout(new FiniteDuration(5, TimeUnit.SECONDS))

    val responseFuture = request.get()
    val responseTry = Await.ready(responseFuture, Duration.Inf).value.get
    responseTry match {
      case Success(t) => {
        if (t.status != 200) {
          logger.error(s"Response from Wowza has status: '${t.status}', body: '${t.body}'")
          return false
        }

        logger.info(s"Successful request to stop composition finished with response status from Wowza: '${t.status}', body: '${t.body}'")
        dao.delete(compositionKey)
        true
      }

      case Failure(e) => {
        logger.error(s"Request to Wowza failed with message: ${e.getMessage}", e)
        false
      }
    }
  }

  /**
    * Parse stream anme from full stream URL
    *
    * @param streamURL stream URL
    * @return stream name
    */
  private def parseStreamName(streamURL: String): String = {
    val urlParts = streamURL.split("/")

    var streamNameIndex: Int = urlParts.length - 1
    if (urlParts(streamNameIndex).isEmpty && streamNameIndex > 0) streamNameIndex -= 1

    urlParts{streamNameIndex}
  }

}
