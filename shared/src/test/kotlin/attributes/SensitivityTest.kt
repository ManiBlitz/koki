package attributes

import kotlin.test.Test
import kotlin.test.assertEquals

class SensitivityTest {

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns REDACTED for a normal string`() {
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("secret-value"))
  }

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns REDACTED regardless of input length`() {
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("a"))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("very long sensitive string"))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate(""))
  }

  @Test
  fun `SENSITIVE obfuscate keeps first two characters and appends four asterisks`() {
    assertEquals("he****", Sensitivity.SENSITIVE.obfuscate("hello"))
  }

  @Test
  fun `SENSITIVE obfuscate works with exactly two characters`() {
    assertEquals("ab****", Sensitivity.SENSITIVE.obfuscate("ab"))
  }

  @Test
  fun `SENSITIVE obfuscate works with a long string`() {
    assertEquals("jo****", Sensitivity.SENSITIVE.obfuscate("john.doe@example.com"))
  }

  @Test
  fun `SAFE obfuscate returns the value unchanged`() {
    val value = "public-attribute-value"
    assertEquals(value, Sensitivity.SAFE.obfuscate(value))
  }

  @Test
  fun `SAFE obfuscate returns empty string unchanged`() {
    assertEquals("", Sensitivity.SAFE.obfuscate(""))
  }

  @Test
  fun `enum has exactly three values`() {
    assertEquals(3, Sensitivity.entries.size)
  }

  @Test
  fun `enum values are in expected order`() {
    val values = Sensitivity.entries
    assertEquals(Sensitivity.HIGHLY_SENSITIVE, values[0])
    assertEquals(Sensitivity.SENSITIVE, values[1])
    assertEquals(Sensitivity.SAFE, values[2])
  }
}
