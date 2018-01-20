package com.mobcrush.composition.dao

import com.mobcrush.composition.model.Composition

trait CompositionDAO {

  def add(compositionKey: String, composition: Composition)

  def get(compositionKey: String): Composition

  def update(compositionKey: String, composition: Composition)

  def delete(compositionKey: String)

}
