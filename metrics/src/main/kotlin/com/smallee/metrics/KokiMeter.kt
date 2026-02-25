package com.smallee.metrics

/**
 * Central interface for our meter definition.
 *
 * A meter is scoped to a single instrumentation scope (typically a class or library) and acts as
 * the single entry point for creating all metric instruments within that scope. Obtaining one meter
 * per class and deriving all counters, gauges, and histograms from it avoids repeating the scope on
 * every instrument definition.
 *
 * ```
 * private val meter = MetricFactory.meter<PaymentService>()
 *
 * val requestCount = meter.counterBuilder("http.requests")
 *     .description("Total inbound HTTP requests")
 *     .unit("{request}")
 *     .build()
 *
 * val latency = meter.histogramBuilder("http.duration")
 *     .description("End-to-end request latency")
 *     .unit("ms")
 *     .build()
 * ```
 *
 * @see MetricFactory
 * @see KokiCounter
 * @see KokiGauge
 * @see KokiHistogram
 */
interface KokiMeter {

  /** Returns the instrumentation scope name of this meter */
  fun getName(): String

  /**
   * Returns a [CounterBuilder] for a [KokiCounter] within this meter's instrumentation scope
   *
   * @param name the instrument name (e.g. "http.requests")
   * @see CounterBuilder
   * @see KokiCounter
   */
  fun counterBuilder(name: String): CounterBuilder

  /**
   * Returns a [GaugeBuilder] for a [KokiGauge] within this meter's instrumentation scope
   *
   * @param name the instrument name (e.g. "memory.usage")
   * @see GaugeBuilder
   * @see KokiGauge
   */
  fun gaugeBuilder(name: String): GaugeBuilder

  /**
   * Returns a [HistogramBuilder] for a [KokiHistogram] within this meter's instrumentation scope
   *
   * @param name the instrument name (e.g. "request.duration")
   * @see HistogramBuilder
   * @see KokiHistogram
   */
  fun histogramBuilder(name: String): HistogramBuilder
}
