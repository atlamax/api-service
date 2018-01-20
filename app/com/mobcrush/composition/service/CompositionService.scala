package com.mobcrush.composition.service

import javax.annotation.{Nonnull, Nullable}

import com.mobcrush.composition.model.{Composition, CompositionResponseModel, CreateCompositionModel}

trait CompositionService {

  @Nonnull
  def create(model: CreateCompositionModel): CompositionResponseModel

  @Nullable
  def attach(compositionKey: String): CompositionResponseModel

  @Nullable
  def start(compositionKey: String): CompositionResponseModel

  def stop(compositionKey: String): Boolean

}
