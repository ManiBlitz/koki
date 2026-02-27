package com.smallee.metrics

import com.smallee.attributes.AttributeEntry
import io.opentelemetry.api.metrics.DoubleGauge

/**
 * Main gauge. Implements [KokiGauge] by wrapping an OpenTelemetry [DoubleGauge].
 *
 * @param name the name of the gauge instrument
 * @param delegate the underlying OpenTelemetry [DoubleGauge]
 * @param baseTags attribute entries attached to every measurement; merged with per-call attributes
 * @see KokiGauge
 * @see AttributeEntry
 */
class Gauge(
  private val name: String,
  private val delegate: DoubleGauge,
  private val baseTags: List<AttributeEntry<*>> = emptyList(),
) : KokiGauge {

  override fun getName(): String = name

  override fun record(value: Double, vararg attributes: AttributeEntry<*>) {
    delegate.set(value, MetricAttributes.build(baseTags + attributes))
  }
}
