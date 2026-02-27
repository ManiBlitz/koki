package com.smallee.metrics

import com.smallee.attributes.AttributeEntry
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
    require(value >= 0) {
      "Counter '$name' received negative value $value; OpenTelemetry counters are monotonically increasing and only accept non-negative values"
    }
    delegate.add(value, MetricAttributes.build(baseTags + attributes))
  }
}
