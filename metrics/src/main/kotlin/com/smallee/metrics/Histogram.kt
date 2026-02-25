package com.smallee.metrics

import com.smallee.attributes.AttributeEntry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.DoubleHistogram

/**
 * Main histogram. Implements [KokiHistogram] by wrapping an OpenTelemetry [DoubleHistogram].
 *
 * @param name the name of the histogram instrument
 * @param delegate the underlying OpenTelemetry [DoubleHistogram]
 * @param baseTags attribute entries attached to every measurement; merged with per-call attributes
 * @see KokiHistogram
 * @see AttributeEntry
 */
class Histogram(
  private val name: String,
  private val delegate: DoubleHistogram,
  private val baseTags: List<AttributeEntry<*>> = emptyList(),
) : KokiHistogram {

  override fun getName(): String = name

  override fun record(value: Double, vararg attributes: AttributeEntry<*>) {
    delegate.record(value, buildAttributes(baseTags + attributes))
  }

  private fun buildAttributes(entries: List<AttributeEntry<*>>): Attributes {
    val builder = Attributes.builder()
    entries.forEach { entry ->
      if (entry.value != null) {
        @Suppress("UNCHECKED_CAST")
        builder.put(entry.definition as AttributeKey<Any>, entry.value as Any)
      }
    }
    return builder.build()
  }
}
