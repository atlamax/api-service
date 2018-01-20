package com.mobcrush.composition.factory

import com.google.inject._
import play.api.Configuration

@Singleton
class WowzaURLFactory @Inject()(config: Configuration) {

  /**
    * Configuration parameters path
    */
  private val RTMP_URL_TEMPLATE: String = "rtmp://"
  private val HTTP_URL_TEMPLATE: String = "http://"

  private val WOWZA_GLOBAL_PATH: String = "wowza"
  private val WOWZA_HOST_PATH: String = WOWZA_GLOBAL_PATH + ".host"

  private val WOWZA_RTMP_PATH: String = WOWZA_GLOBAL_PATH + ".rtmp"
  private val WOWZA_RTMP_PORT_PATH: String = WOWZA_RTMP_PATH + ".port"
  private val WOWZA_RTMP_APPLICATION_PATH: String = WOWZA_RTMP_PATH + ".application"

  private val WOWZA_HTTP_PATH: String = WOWZA_GLOBAL_PATH + ".http"
  private val WOWZA_HTTP_PORT_PATH: String = WOWZA_HTTP_PATH + ".port"
  private val WOWZA_HTTP_PATHWAYS_PATH: String = WOWZA_HTTP_PATH + ".path"
  private val WOWZA_HTTP_START_COMPOSITION_ENDPOINT_PATH: String = WOWZA_HTTP_PATHWAYS_PATH + ".startComposition"
  private val WOWZA_HTTP_STOP_COMPOSITION_ENDPOINT_PATH: String = WOWZA_HTTP_PATHWAYS_PATH + ".stopComposition"

  /**
    * Configuration parameters value
    */
  private val WOWZA_HOST_VALUE: String = config.get[String](WOWZA_HOST_PATH)

  private val WOWZA_RTMP_PORT_VALUE: Int = config.get[Int](WOWZA_RTMP_PORT_PATH)
  private val WOWZA_RTMP_APPLICATION_VALUE: String = config.get[String](WOWZA_RTMP_APPLICATION_PATH)

  private val WOWZA_HTTP_PORT_VALUE: Int = config.get[Int](WOWZA_HTTP_PORT_PATH)
  private val WOWZA_HTTP_START_COMPOSITION_ENDPOINT_VALUE: String = config.get[String](WOWZA_HTTP_START_COMPOSITION_ENDPOINT_PATH)
  private val WOWZA_HTTP_STOP_COMPOSITION_ENDPOINT_VALUE: String = config.get[String](WOWZA_HTTP_STOP_COMPOSITION_ENDPOINT_PATH)

  def createRTMP(streamName: String): String = {
    RTMP_URL_TEMPLATE + WOWZA_HOST_VALUE + ":" + WOWZA_RTMP_PORT_VALUE + "/" + WOWZA_RTMP_APPLICATION_VALUE + "/" + streamName
  }

  def createStartCompositionHTTP(): String = {
    HTTP_URL_TEMPLATE + WOWZA_HOST_VALUE + ":" + WOWZA_HTTP_PORT_VALUE + "/" + WOWZA_HTTP_START_COMPOSITION_ENDPOINT_VALUE
  }

  def createStopCompositionHTTP(): String = {
    HTTP_URL_TEMPLATE + WOWZA_HOST_VALUE + ":" + WOWZA_HTTP_PORT_VALUE + "/" + WOWZA_HTTP_STOP_COMPOSITION_ENDPOINT_VALUE
  }
}
