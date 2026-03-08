package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry
import com.smallee.attributes.Sensitivity
import io.mockk.mockk
import io.mockk.verify
import io.opentelemetry.api.metrics.DoubleGauge
import kotlin.test.Test
import kotlin.test.assertEquals

class GaugeTest {

  private val delegate = mockk<DoubleGauge>(relaxed = true)

  private val pool = AttributeDefinition.createString("gt.pool", Sensitivity.SAFE, false)
  private val region = AttributeDefinition.createString("gt.region", Sensitivity.SAFE, false)
  private val active = AttributeDefinition.createBoolean("gt.active", Sensitivity.SAFE, false)

  // ── getName ────────────────────────────────────────────────────────────────

  @Test
  fun `getName returns the instrument name passed at construction`() {
    val gauge = Gauge("memory.usage", delegate)
    assertEquals("memory.usage", gauge.getName())
  }

  // ── record ─────────────────────────────────────────────────────────────────

  @Test
  fun `record forwards the exact value to delegate set`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(1024.5)
    verify { delegate.set(1024.5, any()) }
  }

  @Test
  fun `record forwards a String attribute value to the delegate`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(512.0, pool("heap"))
    verify { delegate.set(512.0, match { attrs -> attrs.get(pool) == "heap" }) }
  }

  @Test
  fun `record forwards a Boolean attribute value to the delegate`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(128.0, active(true))
    verify { delegate.set(128.0, match { attrs -> attrs.get(active) == true }) }
  }

  @Test
  fun `record forwards multiple attribute values to the delegate`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(256.0, pool("metaspace"), active(false))
    verify {
      delegate.set(
        256.0,
        match { attrs -> attrs.get(pool) == "metaspace" && attrs.get(active) == false },
      )
    }
  }

  @Test
  fun `record merges baseTags with per-call attributes`() {
    val gauge = Gauge("memory.usage", delegate, listOf(region("us-west-2")))
    gauge.record(2048.0, pool("non-heap"))
    verify {
      delegate.set(
        2048.0,
        match { attrs -> attrs.get(region) == "us-west-2" && attrs.get(pool) == "non-heap" },
      )
    }
  }

  @Test
  fun `record with only baseTags and no per-call attributes includes the baseTags`() {
    val gauge = Gauge("memory.usage", delegate, listOf(region("eu-central-1")))
    gauge.record(64.0)
    verify { delegate.set(64.0, match { attrs -> attrs.get(region) == "eu-central-1" }) }
  }

  @Test
  fun `record with no baseTags and no per-call attributes passes empty Attributes`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(32.0)
    verify { delegate.set(32.0, match { attrs -> attrs.isEmpty }) }
  }

  @Test
  fun `record skips an attribute whose value is null`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(64.0, AttributeEntry(pool, null))
    verify { delegate.set(64.0, match { attrs -> attrs.get(pool) == null }) }
  }

  @Test
  fun `record accepts zero as a valid measurement`() {
    val gauge = Gauge("memory.usage", delegate)
    gauge.record(0.0)
    verify { delegate.set(0.0, any()) }
  }

  @Test
  fun `record accepts negative values`() {
    val gauge = Gauge("temperature", delegate)
    gauge.record(-10.5)
    verify { delegate.set(-10.5, any()) }
  }
}
