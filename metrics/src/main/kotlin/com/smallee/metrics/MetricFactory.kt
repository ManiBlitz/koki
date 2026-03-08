package com.smallee.metrics

/**
 * MetricFactory providing the two entry points into the metrics API:
 *
 * **Preferred — meter-scoped** (one meter per class, instruments derived from it):
 * ```
 * private val meter = MetricFactory.meter<PaymentService>()
 * val requests = meter.counterBuilder("http.requests").description("...").build()
 * val latency  = meter.histogramBuilder("http.duration").description("...").build()
 * ```
 *
 * **Convenience — direct builder** (one-off instrument, scope supplied inline):
 * ```
 * val requests = MetricFactory.counterBuilder<PaymentService>("http.requests").build()
 * ```
 *
 * @see KokiMeter
 * @see Meter
 */
object MetricFactory {

  // ── Meter ─────────────────────────────────────────────────────────────────

  /**
   * Returns a [KokiMeter] for the given instrumentation scope
   *
   * @param scope the instrumentation scope name (e.g. a library or component identifier)
   * @see KokiMeter
   * @see Meter
   */
  fun meter(scope: String): KokiMeter = Meter(scope)

  /**
   * Returns a [KokiMeter] using the given class as the instrumentation scope
   *
   * @param clazz the class defining the instrumentation scope
   * @see KokiMeter
   * @see Meter
   */
  fun meter(clazz: Class<*>): KokiMeter = Meter(clazz.name)

  /**
   * Returns a [KokiMeter] using the reified type as the instrumentation scope
   *
   * @see KokiMeter
   * @see Meter
   */
  inline fun <reified T : Any> meter(): KokiMeter = Meter(T::class.java.name)

  // ── Counter ──────────────────────────────────────────────────────────────

  /**
   * Returns a [CounterBuilder] scoped to [scope]. Prefer [meter] when defining multiple instruments
   * for the same scope.
   *
   * @param scope the instrumentation scope name
   * @param name the instrument name (e.g. "http.requests")
   * @see CounterBuilder
   */
  fun counterBuilder(scope: String, name: String): CounterBuilder =
    meter(scope).counterBuilder(name)

  /**
   * Returns a [CounterBuilder] using the given class as the instrumentation scope
   *
   * @param clazz the class defining the instrumentation scope
   * @param name the instrument name
   * @see CounterBuilder
   */
  fun counterBuilder(clazz: Class<*>, name: String): CounterBuilder =
    meter(clazz).counterBuilder(name)

  /**
   * Returns a [CounterBuilder] using the reified type as the instrumentation scope
   *
   * @param name the instrument name
   * @see CounterBuilder
   */
  inline fun <reified T : Any> counterBuilder(name: String): CounterBuilder =
    meter<T>().counterBuilder(name)

  // ── Gauge ─────────────────────────────────────────────────────────────────

  /**
   * Returns a [GaugeBuilder] scoped to [scope]. Prefer [meter] when defining multiple instruments
   * for the same scope.
   *
   * @param scope the instrumentation scope name
   * @param name the instrument name (e.g. "memory.usage")
   * @see GaugeBuilder
   */
  fun gaugeBuilder(scope: String, name: String): GaugeBuilder = meter(scope).gaugeBuilder(name)

  /**
   * Returns a [GaugeBuilder] using the given class as the instrumentation scope
   *
   * @param clazz the class defining the instrumentation scope
   * @param name the instrument name
   * @see GaugeBuilder
   */
  fun gaugeBuilder(clazz: Class<*>, name: String): GaugeBuilder = meter(clazz).gaugeBuilder(name)

  /**
   * Returns a [GaugeBuilder] using the reified type as the instrumentation scope
   *
   * @param name the instrument name
   * @see GaugeBuilder
   */
  inline fun <reified T : Any> gaugeBuilder(name: String): GaugeBuilder =
    meter<T>().gaugeBuilder(name)

  // ── Histogram ─────────────────────────────────────────────────────────────

  /**
   * Returns a [HistogramBuilder] scoped to [scope]. Prefer [meter] when defining multiple
   * instruments for the same scope.
   *
   * @param scope the instrumentation scope name
   * @param name the instrument name (e.g. "request.duration")
   * @see HistogramBuilder
   */
  fun histogramBuilder(scope: String, name: String): HistogramBuilder =
    meter(scope).histogramBuilder(name)

  /**
   * Returns a [HistogramBuilder] using the given class as the instrumentation scope
   *
   * @param clazz the class defining the instrumentation scope
   * @param name the instrument name
   * @see HistogramBuilder
   */
  fun histogramBuilder(clazz: Class<*>, name: String): HistogramBuilder =
    meter(clazz).histogramBuilder(name)

  /**
   * Returns a [HistogramBuilder] using the reified type as the instrumentation scope
   *
   * @param name the instrument name
   * @see HistogramBuilder
   */
  inline fun <reified T : Any> histogramBuilder(name: String): HistogramBuilder =
    meter<T>().histogramBuilder(name)
}
