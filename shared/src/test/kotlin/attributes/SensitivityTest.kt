package attributes

import com.smallee.attributes.Sensitivity
import kotlin.test.Test
import kotlin.test.assertEquals

class SensitivityTest {

  // ── HIGHLY_SENSITIVE ───────────────────────────────────────────────────────

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns REDACTED for any string`() {
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("secret-value"))
  }

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns REDACTED regardless of input length`() {
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("a"))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate("very long sensitive string"))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate(""))
  }

  @Test
  fun `HIGHLY_SENSITIVE obfuscate returns REDACTED for non-string types`() {
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate(42L))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate(true))
    assertEquals("[REDACTED]", Sensitivity.HIGHLY_SENSITIVE.obfuscate(3.14))
  }

  // ── SENSITIVE strings ──────────────────────────────────────────────────────

  @Test
  fun `SENSITIVE obfuscate returns four asterisks for strings shorter than 4 characters`() {
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(""))
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate("a"))
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate("ab"))
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate("abc"))
  }

  @Test
  fun `SENSITIVE obfuscate returns two visible characters for strings of 4 or 6 or more characters`() {
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate("abcd"))
    assertEquals("ab****", Sensitivity.SENSITIVE.obfuscate("abcdef"))
    assertEquals("jo****", Sensitivity.SENSITIVE.obfuscate("john.doe@example.com"))
  }

  @Test
  fun `SENSITIVE obfuscate returns one visible character for strings of exactly 5 characters`() {
    assertEquals("a****", Sensitivity.SENSITIVE.obfuscate("abcde"))
    assertEquals("h****", Sensitivity.SENSITIVE.obfuscate("hello"))
  }

  // ── SENSITIVE booleans ─────────────────────────────────────────────────────

  @Test
  fun `SENSITIVE obfuscate returns four asterisks for booleans`() {
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(true))
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(false))
  }

  // ── SENSITIVE numbers ──────────────────────────────────────────────────────

  @Test
  fun `SENSITIVE obfuscate applies string masking rules to Long values`() {
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(123L)) // "123" → length 3 < 4
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(1234L)) // "1234" → length 4
    assertEquals("1****", Sensitivity.SENSITIVE.obfuscate(12345L)) // "12345" → length 5
    assertEquals("12****", Sensitivity.SENSITIVE.obfuscate(123456L)) // "123456" → length 6
  }

  @Test
  fun `SENSITIVE obfuscate applies string masking rules to Double values`() {
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(1.0)) // "1.0"    → length 3 < 4
    assertEquals("****", Sensitivity.SENSITIVE.obfuscate(3.14)) // "3.14"   → length 4
    assertEquals(
      "3****",
      Sensitivity.SENSITIVE.obfuscate(3.141),
    ) // "3.141"  → length 5 → first char only
    assertEquals("3.****", Sensitivity.SENSITIVE.obfuscate(3.1415)) // "3.1415" → length 6
  }

  // ── SENSITIVE lists ────────────────────────────────────────────────────────

  @Test
  fun `SENSITIVE obfuscate masks each string element of a list individually`() {
    val result = Sensitivity.SENSITIVE.obfuscate(listOf("ab", "hello", "john.doe@example.com"))
    assertEquals("****, h****, jo****", result)
  }

  @Test
  fun `SENSITIVE obfuscate masks each Long element of a list individually`() {
    val result = Sensitivity.SENSITIVE.obfuscate(listOf(123L, 12345L, 123456L))
    assertEquals("****, 1****, 12****", result)
  }

  @Test
  fun `SENSITIVE obfuscate masks each boolean element of a list individually`() {
    val result = Sensitivity.SENSITIVE.obfuscate(listOf(true, false))
    assertEquals("****, ****", result)
  }

  // ── SAFE ───────────────────────────────────────────────────────────────────

  @Test
  fun `SAFE obfuscate returns the string value unchanged`() {
    val value = "public-attribute-value"
    assertEquals(value, Sensitivity.SAFE.obfuscate(value))
  }

  @Test
  fun `SAFE obfuscate returns empty string unchanged`() {
    assertEquals("", Sensitivity.SAFE.obfuscate(""))
  }

  @Test
  fun `SAFE obfuscate returns string representation for non-string types`() {
    assertEquals("42", Sensitivity.SAFE.obfuscate(42L))
    assertEquals("true", Sensitivity.SAFE.obfuscate(true))
    assertEquals("3.14", Sensitivity.SAFE.obfuscate(3.14))
  }

  // ── enum structure ─────────────────────────────────────────────────────────

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
