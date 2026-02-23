package attributes

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.Sensitivity
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Verifies [Sensitivity] behaviour when obfuscation is disabled (isObfuscationEnabled = false). */
class SensitivityObfuscationDisabledTest {

  @BeforeTest
  fun setUp() {
    mockkObject(AttributeDefinition.Companion)
    every { AttributeDefinition.isObfuscationEnabled } returns false
  }

  @AfterTest
  fun tearDown() {
    unmockkObject(AttributeDefinition.Companion)
  }

  // ── HIGHLY_SENSITIVE ───────────────────────────────────────────────────────

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns plain string when obfuscation is disabled`() {
    assertEquals("secret-value", Sensitivity.HIGHLY_SENSITIVE.obfuscate("secret-value"))
  }

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns toString for non-string types when obfuscation is disabled`() {
    assertEquals("42", Sensitivity.HIGHLY_SENSITIVE.obfuscate(42L))
    assertEquals("true", Sensitivity.HIGHLY_SENSITIVE.obfuscate(true))
    assertEquals("3.14", Sensitivity.HIGHLY_SENSITIVE.obfuscate(3.14))
  }

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns null string for null value when obfuscation is disabled`() {
    assertEquals("null", Sensitivity.HIGHLY_SENSITIVE.obfuscate(null))
  }

  // ── SENSITIVE ──────────────────────────────────────────────────────────────

  @Test
  fun `SENSITIVE obfuscate returns plain string when obfuscation is disabled`() {
    assertEquals("secret", Sensitivity.SENSITIVE.obfuscate("secret"))
  }

  @Test
  fun `SENSITIVE obfuscate returns toString for non-string types when obfuscation is disabled`() {
    assertEquals("42", Sensitivity.SENSITIVE.obfuscate(42L))
    assertEquals("true", Sensitivity.SENSITIVE.obfuscate(true))
    assertEquals("3.14", Sensitivity.SENSITIVE.obfuscate(3.14))
  }

  @Test
  fun `SENSITIVE obfuscate returns list toString when obfuscation is disabled`() {
    val list = listOf("hello", "world")
    assertEquals(list.toString(), Sensitivity.SENSITIVE.obfuscate(list))
  }

  @Test
  fun `SENSITIVE obfuscate returns null string for null value when obfuscation is disabled`() {
    assertEquals("null", Sensitivity.SENSITIVE.obfuscate(null))
  }
}
