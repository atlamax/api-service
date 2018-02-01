package com.mobcrush.composition.service

import javax.annotation.{Nonnull, Nullable}

import com.mobcrush.composition.model.{Composition, CompositionResponseModel, CreateCompositionModel, StartCompositionModel}

trait CompositionService {

  @Nonnull
  def create(model: CreateCompositionModel): CompositionResponseModel

  @Nullable
  def attach(compositionKey: String): CompositionResponseModel

  @Nullable
  def start(compositionKey: String, startCompositionModel: StartCompositionModel): Unit

  def stop(compositionKey: String): Boolean

}
