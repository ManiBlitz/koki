package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry
import com.smallee.attributes.Sensitivity
import io.mockk.mockk
import io.mockk.verify
import io.opentelemetry.api.metrics.LongCounter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CounterTest {

  private val delegate = mockk<LongCounter>(relaxed = true)

  // Unique attribute names to avoid registry collisions across test classes
  private val httpMethod =
    AttributeDefinition.createString("ct.http.method", Sensitivity.SAFE, false)
  private val statusCode = AttributeDefinition.createLong("ct.status.code", Sensitivity.SAFE, false)
  private val region = AttributeDefinition.createString("ct.region", Sensitivity.SAFE, false)

  // ── getName ────────────────────────────────────────────────────────────────

  @Test
  fun `getName returns the instrument name passed at construction`() {
    val counter = Counter("http.requests", delegate)
    assertEquals("http.requests", counter.getName())
  }

  // ── add ────────────────────────────────────────────────────────────────────

  @Test
  fun `add forwards the exact value to the delegate`() {
    val counter = Counter("requests", delegate)
    counter.add(7L)
    verify { delegate.add(7L, any()) }
  }

  @Test
  fun `add forwards a String attribute value to the delegate`() {
    val counter = Counter("requests", delegate)
    counter.add(1L, AttributeEntry(httpMethod, "GET"))
    verify { delegate.add(1L, match { attrs -> attrs.get(httpMethod) == "GET" }) }
  }

  @Test
  fun `add forwards a Long attribute value to the delegate`() {
    val counter = Counter("requests", delegate)
    counter.add(1L, AttributeEntry(statusCode, 201L))
    verify { delegate.add(1L, match { attrs -> attrs.get(statusCode) == 201L }) }
  }

  @Test
  fun `add forwards multiple attribute values to the delegate`() {
    val counter = Counter("requests", delegate)
    counter.add(3L, AttributeEntry(httpMethod, "DELETE"), AttributeEntry(statusCode, 204L))
    verify {
      delegate.add(
        3L,
        match { attrs -> attrs.get(httpMethod) == "DELETE" && attrs.get(statusCode) == 204L },
      )
    }
  }

  @Test
  fun `add merges baseTags with per-call attributes`() {
    val counter = Counter("requests", delegate, listOf(AttributeEntry(region, "eu-west-1")))
    counter.add(2L, AttributeEntry(httpMethod, "POST"))
    verify {
      delegate.add(
        2L,
        match { attrs -> attrs.get(region) == "eu-west-1" && attrs.get(httpMethod) == "POST" },
      )
    }
  }

  @Test
  fun `add with only baseTags and no per-call attributes includes the baseTags`() {
    val counter = Counter("requests", delegate, listOf(AttributeEntry(region, "us-east-1")))
    counter.add(5L)
    verify { delegate.add(5L, match { attrs -> attrs.get(region) == "us-east-1" }) }
  }

  @Test
  fun `add with no baseTags and no per-call attributes passes empty Attributes`() {
    val counter = Counter("requests", delegate)
    counter.add(1L)
    verify { delegate.add(1L, match { attrs -> attrs.isEmpty }) }
  }

  @Test
  fun `add skips an attribute whose value is null`() {
    val counter = Counter("requests", delegate)
    counter.add(1L, AttributeEntry(httpMethod, null))
    verify { delegate.add(1L, match { attrs -> attrs.get(httpMethod) == null }) }
  }

  @Test
  fun `add throws IllegalArgumentException for a negative value`() {
    val counter = Counter("requests", delegate)
    assertFailsWith<IllegalArgumentException> { counter.add(-1L) }
  }

  // ── increment ──────────────────────────────────────────────────────────────

  @Test
  fun `increment delegates to add with value 1`() {
    val counter = Counter("requests", delegate)
    counter.increment()
    verify { delegate.add(1L, any()) }
  }

  @Test
  fun `increment forwards per-call attributes to the delegate`() {
    val counter = Counter("requests", delegate)
    counter.increment(AttributeEntry(httpMethod, "PATCH"))
    verify { delegate.add(1L, match { attrs -> attrs.get(httpMethod) == "PATCH" }) }
  }

  @Test
  fun `increment merges baseTags with per-call attributes`() {
    val counter = Counter("requests", delegate, listOf(AttributeEntry(region, "ap-southeast-1")))
    counter.increment(AttributeEntry(statusCode, 200L))
    verify {
      delegate.add(
        1L,
        match { attrs -> attrs.get(region) == "ap-southeast-1" && attrs.get(statusCode) == 200L },
      )
    }
  }
}
