package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry
import com.smallee.attributes.Sensitivity
import io.mockk.mockk
import io.mockk.verify
import io.opentelemetry.api.metrics.DoubleHistogram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HistogramTest {

  private val delegate = mockk<DoubleHistogram>(relaxed = true)

  private val endpoint = AttributeDefinition.createString("ht.endpoint", Sensitivity.SAFE, false)
  private val region = AttributeDefinition.createString("ht.region", Sensitivity.SAFE, false)
  private val statusCode = AttributeDefinition.createLong("ht.status.code", Sensitivity.SAFE, false)

  // ── getName ────────────────────────────────────────────────────────────────

  @Test
  fun `getName returns the instrument name passed at construction`() {
    val histogram = Histogram("http.duration", delegate)
    assertEquals("http.duration", histogram.getName())
  }

  // ── record ─────────────────────────────────────────────────────────────────

  @Test
  fun `record forwards the exact value to delegate record`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(42.5)
    verify { delegate.record(42.5, any()) }
  }

  @Test
  fun `record forwards a String attribute value to the delegate`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(10.0, AttributeEntry(endpoint, "/api/users"))
    verify { delegate.record(10.0, match { attrs -> attrs.get(endpoint) == "/api/users" }) }
  }

  @Test
  fun `record forwards a Long attribute value to the delegate`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(15.0, AttributeEntry(statusCode, 200L))
    verify { delegate.record(15.0, match { attrs -> attrs.get(statusCode) == 200L }) }
  }

  @Test
  fun `record forwards multiple attribute values to the delegate`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(
      33.0,
      AttributeEntry(endpoint, "/api/payments"),
      AttributeEntry(statusCode, 201L),
    )
    verify {
      delegate.record(
        33.0,
        match { attrs -> attrs.get(endpoint) == "/api/payments" && attrs.get(statusCode) == 201L },
      )
    }
  }

  @Test
  fun `record merges baseTags with per-call attributes`() {
    val histogram =
      Histogram("http.duration", delegate, listOf(AttributeEntry(region, "us-east-2")))
    histogram.record(25.0, AttributeEntry(endpoint, "/api/orders"))
    verify {
      delegate.record(
        25.0,
        match { attrs -> attrs.get(region) == "us-east-2" && attrs.get(endpoint) == "/api/orders" },
      )
    }
  }

  @Test
  fun `record with only baseTags and no per-call attributes includes the baseTags`() {
    val histogram =
      Histogram("http.duration", delegate, listOf(AttributeEntry(region, "ap-east-1")))
    histogram.record(5.0)
    verify { delegate.record(5.0, match { attrs -> attrs.get(region) == "ap-east-1" }) }
  }

  @Test
  fun `record with no baseTags and no per-call attributes passes empty Attributes`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(1.0)
    verify { delegate.record(1.0, match { attrs -> attrs.isEmpty }) }
  }

  @Test
  fun `record throws IllegalArgumentException for a negative value`() {
    val histogram = Histogram("http.duration", delegate)
    assertFailsWith<IllegalArgumentException> { histogram.record(-1.0) }
  }

  @Test
  fun `record skips an attribute whose value is null`() {
    val histogram = Histogram("http.duration", delegate)
    histogram.record(15.0, AttributeEntry(endpoint, null))
    verify { delegate.record(15.0, match { attrs -> attrs.get(endpoint) == null }) }
  }
}
