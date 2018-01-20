package com.mobcrush.composition.dao

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import com.google.inject.Singleton
import com.mobcrush.composition.model.Composition

@Singleton
class InMemoryCompositionDAO extends CompositionDAO {

  private val COMPOSITIONS_MAP: ConcurrentMap[String, Composition] = new ConcurrentHashMap[String, Composition]()

  override def add(compositionKey: String, composition: Composition): Unit = {
    COMPOSITIONS_MAP.put(compositionKey, composition)
  }

  override def get(compositionKey: String): Composition = {
    COMPOSITIONS_MAP.get(compositionKey)
  }

  override def update(compositionKey: String, composition: Composition): Unit = {
    COMPOSITIONS_MAP.replace(compositionKey, composition)
  }

  override def delete(compositionKey: String): Unit = {
    COMPOSITIONS_MAP.remove(compositionKey)
  }
}
