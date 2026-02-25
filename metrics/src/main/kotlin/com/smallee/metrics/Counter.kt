package com.smallee.metrics

import com.smallee.attributes.AttributeEntry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.LongCounter

/**
 * Main counter. Implements [KokiCounter] by wrapping an OpenTelemetry [LongCounter].
 *
 * @param name the name of the counter instrument
 * @param delegate the underlying OpenTelemetry [LongCounter]
 * @param baseTags attribute entries attached to every measurement; merged with per-call attributes
 * @see KokiCounter
 * @see AttributeEntry
 */
class Counter(
  private val name: String,
  private val delegate: LongCounter,
  private val baseTags: List<AttributeEntry<*>> = emptyList(),
) : KokiCounter {

  override fun getName(): String = name

  override fun add(value: Long, vararg attributes: AttributeEntry<*>) {
    delegate.add(value, buildAttributes(baseTags + attributes))
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
