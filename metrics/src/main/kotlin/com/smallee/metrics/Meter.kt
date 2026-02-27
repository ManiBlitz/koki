package com.smallee.metrics

import io.opentelemetry.api.GlobalOpenTelemetry

/**
 * Main meter. Implements [KokiMeter] by obtaining an OpenTelemetry meter for the given
 * instrumentation scope and delegating all instrument construction to it.
 *
 * @param scope the instrumentation scope name (typically a fully-qualified class name)
 * @see KokiMeter
 * @see MetricFactory
 */
class Meter(private val scope: String) : KokiMeter {

  private val meter = GlobalOpenTelemetry.getMeter(scope)

  override fun getName(): String = scope

  override fun counterBuilder(name: String): CounterBuilder =
    MetricBuilder { description, unit, baseTags ->
      val delegate = meter.counterBuilder(name).setDescription(description).setUnit(unit).build()
      Counter(name, delegate, baseTags)
    }

  override fun gaugeBuilder(name: String): GaugeBuilder =
    MetricBuilder { description, unit, baseTags ->
      val delegate = meter.gaugeBuilder(name).setDescription(description).setUnit(unit).build()
      Gauge(name, delegate, baseTags)
    }

  override fun histogramBuilder(name: String): HistogramBuilder =
    MetricBuilder { description, unit, baseTags ->
      val delegate = meter.histogramBuilder(name).setDescription(description).setUnit(unit).build()
      Histogram(name, delegate, baseTags)
    }
}
