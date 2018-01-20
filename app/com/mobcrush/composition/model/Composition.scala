package com.mobcrush.composition.model

case class Composition(slaveStreamURL: String, var masterStreamURL: String = "", var targetStreamURL: String = "")
