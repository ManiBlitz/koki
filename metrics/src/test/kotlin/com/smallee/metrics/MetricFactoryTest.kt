package com.smallee.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MetricFactoryTest {

  private class SampleService

  // ── meter ──────────────────────────────────────────────────────────────────

  @Test
  fun `meter by String returns a KokiMeter with the given scope`() {
    val meter = MetricFactory.meter("com.my.Service")
    assertIs<KokiMeter>(meter)
    assertEquals("com.my.Service", meter.getName())
  }

  @Test
  fun `meter by Class returns a KokiMeter scoped to the class name`() {
    val meter = MetricFactory.meter(SampleService::class.java)
    assertEquals(SampleService::class.java.name, meter.getName())
  }

  @Test
  fun `meter by reified type returns a KokiMeter scoped to the type name`() {
    val meter = MetricFactory.meter<SampleService>()
    assertEquals(SampleService::class.java.name, meter.getName())
  }

  @Test
  fun `meter by Class and meter by reified type produce the same scope`() {
    val byClass = MetricFactory.meter(SampleService::class.java)
    val byReified = MetricFactory.meter<SampleService>()
    assertEquals(byClass.getName(), byReified.getName())
  }

  // ── counterBuilder ─────────────────────────────────────────────────────────

  @Test
  fun `counterBuilder by String builds a KokiCounter with the correct instrument name`() {
    val counter = MetricFactory.counterBuilder("com.test", "http.requests").build()
    assertIs<KokiCounter>(counter)
    assertEquals("http.requests", counter.getName())
  }

  @Test
  fun `counterBuilder by Class builds a KokiCounter with the correct instrument name`() {
    val counter = MetricFactory.counterBuilder(SampleService::class.java, "db.queries").build()
    assertIs<KokiCounter>(counter)
    assertEquals("db.queries", counter.getName())
  }

  @Test
  fun `counterBuilder by reified type builds a KokiCounter with the correct instrument name`() {
    val counter = MetricFactory.counterBuilder<SampleService>("cache.hits").build()
    assertIs<KokiCounter>(counter)
    assertEquals("cache.hits", counter.getName())
  }

  @Test
  fun `counterBuilder by String and by reified type with same names produce equal instrument names`() {
    val byString = MetricFactory.counterBuilder("com.test", "events").build()
    val byReified = MetricFactory.counterBuilder<SampleService>("events").build()
    assertEquals(byString.getName(), byReified.getName())
  }

  // ── gaugeBuilder ───────────────────────────────────────────────────────────

  @Test
  fun `gaugeBuilder by String builds a KokiGauge with the correct instrument name`() {
    val gauge = MetricFactory.gaugeBuilder("com.test", "memory.usage").build()
    assertIs<KokiGauge>(gauge)
    assertEquals("memory.usage", gauge.getName())
  }

  @Test
  fun `gaugeBuilder by Class builds a KokiGauge with the correct instrument name`() {
    val gauge = MetricFactory.gaugeBuilder(SampleService::class.java, "thread.count").build()
    assertIs<KokiGauge>(gauge)
    assertEquals("thread.count", gauge.getName())
  }

  @Test
  fun `gaugeBuilder by reified type builds a KokiGauge with the correct instrument name`() {
    val gauge = MetricFactory.gaugeBuilder<SampleService>("queue.size").build()
    assertIs<KokiGauge>(gauge)
    assertEquals("queue.size", gauge.getName())
  }

  // ── histogramBuilder ───────────────────────────────────────────────────────

  @Test
  fun `histogramBuilder by String builds a KokiHistogram with the correct instrument name`() {
    val histogram = MetricFactory.histogramBuilder("com.test", "request.duration").build()
    assertIs<KokiHistogram>(histogram)
    assertEquals("request.duration", histogram.getName())
  }

  @Test
  fun `histogramBuilder by Class builds a KokiHistogram with the correct instrument name`() {
    val histogram = MetricFactory.histogramBuilder(SampleService::class.java, "batch.size").build()
    assertIs<KokiHistogram>(histogram)
    assertEquals("batch.size", histogram.getName())
  }

  @Test
  fun `histogramBuilder by reified type builds a KokiHistogram with the correct instrument name`() {
    val histogram = MetricFactory.histogramBuilder<SampleService>("response.size").build()
    assertIs<KokiHistogram>(histogram)
    assertEquals("response.size", histogram.getName())
  }
}
