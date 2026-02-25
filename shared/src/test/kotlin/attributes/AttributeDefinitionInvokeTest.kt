package attributes

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.Sensitivity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AttributeDefinitionInvokeTest {

  // Unique prefixes to avoid collisions with the singleton registry
  private val httpMethod =
    AttributeDefinition.createString("inv.http.method", Sensitivity.SAFE, false)
  private val statusCode =
    AttributeDefinition.createLong("inv.status.code", Sensitivity.SAFE, false)
  private val temperature =
    AttributeDefinition.createDouble("inv.temperature", Sensitivity.SAFE, false)
  private val active = AttributeDefinition.createBoolean("inv.active", Sensitivity.SAFE, false)

  // ── invoke ─────────────────────────────────────────────────────────────────

  @Test
  fun `invoke on a String definition produces an entry with the correct value`() {
    val entry = httpMethod("GET")
    assertEquals("GET", entry.value)
  }

  @Test
  fun `invoke on a Long definition produces an entry with the correct value`() {
    val entry = statusCode(200L)
    assertEquals(200L, entry.value)
  }

  @Test
  fun `invoke on a Double definition produces an entry with the correct value`() {
    val entry = temperature(36.6)
    assertEquals(36.6, entry.value)
  }

  @Test
  fun `invoke on a Boolean definition produces an entry with the correct value`() {
    val entry = active(true)
    assertEquals(true, entry.value)
  }

  @Test
  fun `invoke preserves the exact definition reference in the produced entry`() {
    val entry = httpMethod("POST")
    assertSame(httpMethod, entry.definition)
  }

  @Test
  fun `invoke with different values produces distinct entries`() {
    val get = httpMethod("GET")
    val post = httpMethod("POST")
    assertEquals("GET", get.value)
    assertEquals("POST", post.value)
  }
}
