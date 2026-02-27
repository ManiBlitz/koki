package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.Sensitivity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MeterTest {

  // ── getName ────────────────────────────────────────────────────────────────

  @Test
  fun `getName returns the instrumentation scope passed at construction`() {
    val meter = Meter("com.smallee.payments")
    assertEquals("com.smallee.payments", meter.getName())
  }

  @Test
  fun `getName returns the fully qualified class name as scope`() {
    val meter = Meter(MeterTest::class.java.name)
    assertEquals("com.smallee.metrics.MeterTest", meter.getName())
  }

  // ── counterBuilder ─────────────────────────────────────────────────────────

  @Test
  fun `counterBuilder returns a non-null CounterBuilder`() {
    assertNotNull(Meter("com.test").counterBuilder("requests"))
  }

  @Test
  fun `counterBuilder build returns a KokiCounter`() {
    val counter = Meter("com.test").counterBuilder("requests").build()
    assertIs<KokiCounter>(counter)
  }

  @Test
  fun `counterBuilder build returns a Counter`() {
    val counter = Meter("com.test").counterBuilder("requests").build()
    assertIs<Counter>(counter)
  }

  @Test
  fun `built counter carries the instrument name`() {
    val counter = Meter("com.test").counterBuilder("http.requests").build()
    assertEquals("http.requests", counter.getName())
  }

  @Test
  fun `counter add does not throw`() {
    Meter("com.test").counterBuilder("requests").build().add(1L)
  }

  @Test
  fun `counter increment does not throw`() {
    Meter("com.test").counterBuilder("requests").build().increment()
  }

  // ── gaugeBuilder ───────────────────────────────────────────────────────────

  @Test
  fun `gaugeBuilder returns a non-null GaugeBuilder`() {
    assertNotNull(Meter("com.test").gaugeBuilder("memory"))
  }

  @Test
  fun `gaugeBuilder build returns a KokiGauge`() {
    val gauge = Meter("com.test").gaugeBuilder("memory.usage").build()
    assertIs<KokiGauge>(gauge)
  }

  @Test
  fun `gaugeBuilder build returns a Gauge`() {
    val gauge = Meter("com.test").gaugeBuilder("memory.usage").build()
    assertIs<Gauge>(gauge)
  }

  @Test
  fun `built gauge carries the instrument name`() {
    val gauge = Meter("com.test").gaugeBuilder("jvm.memory").build()
    assertEquals("jvm.memory", gauge.getName())
  }

  @Test
  fun `gauge record does not throw`() {
    Meter("com.test").gaugeBuilder("memory").build().record(1024.0)
  }

  // ── histogramBuilder ───────────────────────────────────────────────────────

  @Test
  fun `histogramBuilder returns a non-null HistogramBuilder`() {
    assertNotNull(Meter("com.test").histogramBuilder("latency"))
  }

  @Test
  fun `histogramBuilder build returns a KokiHistogram`() {
    val histogram = Meter("com.test").histogramBuilder("http.duration").build()
    assertIs<KokiHistogram>(histogram)
  }

  @Test
  fun `histogramBuilder build returns a Histogram`() {
    val histogram = Meter("com.test").histogramBuilder("http.duration").build()
    assertIs<Histogram>(histogram)
  }

  @Test
  fun `built histogram carries the instrument name`() {
    val histogram = Meter("com.test").histogramBuilder("request.duration").build()
    assertEquals("request.duration", histogram.getName())
  }

  @Test
  fun `histogram record does not throw`() {
    Meter("com.test").histogramBuilder("duration").build().record(42.5)
  }

  // ── builder configuration is respected ────────────────────────────────────

  @Test
  fun `counterBuilder description and unit are accepted without throwing`() {
    val def = AttributeDefinition.createString("mt.env", Sensitivity.SAFE, false)
    Meter("com.test")
      .counterBuilder("events")
      .description("Total events processed")
      .unit("{event}")
      .addTag(def, "prod")
      .build()
      .increment()
  }
}
