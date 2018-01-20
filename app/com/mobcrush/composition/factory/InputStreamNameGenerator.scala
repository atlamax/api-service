package com.mobcrush.composition.factory

import java.util.concurrent.atomic.AtomicInteger

import com.google.inject.Singleton

@Singleton
class InputStreamNameGenerator extends StreamNameGenerator {

  private val BASE_STREAM_NAME: String = "inputStream"

  private val COUNTER: AtomicInteger = new AtomicInteger(1)

  override def generate(): String = {
    BASE_STREAM_NAME + COUNTER.getAndIncrement()
  }
}
