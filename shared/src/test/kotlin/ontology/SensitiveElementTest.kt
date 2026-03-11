package ontology

import com.smallee.ontology.SensitiveCategory
import com.smallee.ontology.SensitiveElement
import com.smallee.ontology.SensitivityDomain
import com.smallee.ontology.SensitivityTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SensitiveElementTest {

  // ── structural invariants ──────────────────────────────────────────────────

  @Test
  fun `every element has a non-blank displayName`() {
    SensitiveElement.entries.forEach { element ->
      assertTrue(
        element.displayName.isNotBlank(),
        "${element.name} must have a non-blank displayName",
      )
    }
  }

  @Test
  fun `every element has a non-blank shortName`() {
    SensitiveElement.entries.forEach { element ->
      assertTrue(element.shortName.isNotBlank(), "${element.name} must have a non-blank shortName")
    }
  }

  @Test
  fun `every element has at least one alias`() {
    SensitiveElement.entries.forEach { element ->
      assertTrue(element.aliases.isNotEmpty(), "${element.name} must declare at least one alias")
    }
  }

  @Test
  fun `every element belongs to exactly one category`() {
    SensitiveElement.entries.forEach { element ->
      assertNotNull(element.category, "${element.name} must have a category")
    }
  }

  @Test
  fun `every element belongs to at least one regulatory domain`() {
    SensitiveElement.entries.forEach { element ->
      assertTrue(element.domains.isNotEmpty(), "${element.name} must belong to at least one domain")
    }
  }

  @Test
  fun `every element has a non-blank maskingReplacement`() {
    SensitiveElement.entries.forEach { element ->
      assertTrue(
        element.maskingReplacement.isNotBlank(),
        "${element.name} must have a non-blank maskingReplacement",
      )
    }
  }

  // ── Tier 0 / Tier 1 full-redaction invariant ───────────────────────────────

  @Test
  fun `all Tier 0 elements use full redaction`() {
    SensitiveElement.entries
      .filter { it.tier == SensitivityTier.TIER_0 }
      .forEach { element ->
        assertEquals(
          "[REDACTED]",
          element.maskingReplacement,
          "${element.name} (TIER_0) must use full redaction",
        )
      }
  }

  @Test
  fun `all Tier 1 elements use full redaction`() {
    SensitiveElement.entries
      .filter { it.tier == SensitivityTier.TIER_1 }
      .forEach { element ->
        assertEquals(
          "[REDACTED]",
          element.maskingReplacement,
          "${element.name} (TIER_1) must use full redaction",
        )
      }
  }

  // ── isPatternDetectable ────────────────────────────────────────────────────

  @Test
  fun `SOCIAL_SECURITY_NUMBER is pattern-detectable`() {
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER.isPatternDetectable)
  }

  @Test
  fun `CREDIT_CARD_NUMBER is pattern-detectable`() {
    assertTrue(SensitiveElement.CREDIT_CARD_NUMBER.isPatternDetectable)
  }

  @Test
  fun `EMAIL_ADDRESS is pattern-detectable`() {
    assertTrue(SensitiveElement.EMAIL_ADDRESS.isPatternDetectable)
  }

  @Test
  fun `FULL_NAME is not pattern-detectable`() {
    assertFalse(SensitiveElement.FULL_NAME.isPatternDetectable)
  }

  @Test
  fun `FINGERPRINT is not pattern-detectable`() {
    assertFalse(SensitiveElement.FINGERPRINT.isPatternDetectable)
  }

  @Test
  fun `HEALTH_CONDITION is not pattern-detectable`() {
    assertFalse(SensitiveElement.HEALTH_CONDITION.isPatternDetectable)
  }

  @Test
  fun `DRIVERS_LICENSE_NUMBER is not pattern-detectable`() {
    assertFalse(SensitiveElement.DRIVERS_LICENSE_NUMBER.isPatternDetectable)
  }

  // ── detect ─────────────────────────────────────────────────────────────────

  @Test
  fun `SSN detect finds a hyphenated SSN`() {
    val matches = SensitiveElement.SOCIAL_SECURITY_NUMBER.detect("SSN: 123-45-6789")
    assertEquals(1, matches.size)
    assertEquals("123-45-6789", matches.first().value)
  }

  @Test
  fun `SSN detect finds an unformatted SSN`() {
    val matches = SensitiveElement.SOCIAL_SECURITY_NUMBER.detect("number 123456789 on file")
    assertEquals(1, matches.size)
    assertEquals("123456789", matches.first().value)
  }

  @Test
  fun `SSN detect finds multiple SSNs in one string`() {
    val matches = SensitiveElement.SOCIAL_SECURITY_NUMBER.detect("123-45-6789 and 987-65-4321")
    assertEquals(2, matches.size)
  }

  @Test
  fun `SSN detect returns empty list when no SSN is present`() {
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER.detect("no sensitive data here").isEmpty())
  }

  @Test
  fun `credit card detect finds a space-separated PAN`() {
    val matches = SensitiveElement.CREDIT_CARD_NUMBER.detect("Card: 4111 1111 1111 1111")
    assertEquals(1, matches.size)
    assertEquals("4111 1111 1111 1111", matches.first().value)
  }

  @Test
  fun `credit card detect finds a hyphen-separated PAN`() {
    val matches = SensitiveElement.CREDIT_CARD_NUMBER.detect("4111-1111-1111-1111")
    assertEquals(1, matches.size)
  }

  @Test
  fun `credit card detect finds an unseparated PAN`() {
    val matches = SensitiveElement.CREDIT_CARD_NUMBER.detect("pan=4111111111111111")
    assertEquals(1, matches.size)
  }

  @Test
  fun `email detect finds a standard email address`() {
    val matches = SensitiveElement.EMAIL_ADDRESS.detect("Contact user@example.com for help")
    assertEquals(1, matches.size)
    assertEquals("user@example.com", matches.first().value)
  }

  @Test
  fun `email detect finds a complex email address`() {
    val matches = SensitiveElement.EMAIL_ADDRESS.detect("test.email+tag@sub.domain.co.uk")
    assertEquals(1, matches.size)
  }

  @Test
  fun `email detect returns empty list for text without email`() {
    assertTrue(SensitiveElement.EMAIL_ADDRESS.detect("no email here").isEmpty())
  }

  @Test
  fun `IP address detect finds an IPv4 address`() {
    val matches = SensitiveElement.IP_ADDRESS.detect("client_ip=192.168.1.100")
    assertEquals(1, matches.size)
    assertEquals("192.168.1.100", matches.first().value)
  }

  @Test
  fun `IBAN detect finds a valid IBAN`() {
    val matches = SensitiveElement.IBAN.detect("IBAN: DE89370400440532013000")
    assertEquals(1, matches.size)
    assertEquals("DE89370400440532013000", matches.first().value)
  }

  @Test
  fun `card expiration date detect finds MM-YY format`() {
    val matches = SensitiveElement.CARD_EXPIRATION_DATE.detect("exp: 12/25")
    assertEquals(1, matches.size)
  }

  @Test
  fun `card expiration date detect finds MM-YYYY format`() {
    val matches = SensitiveElement.CARD_EXPIRATION_DATE.detect("expiry=01/2025")
    assertEquals(1, matches.size)
  }

  @Test
  fun `diagnosis code detect finds an ICD-10 code`() {
    val matches = SensitiveElement.DIAGNOSIS_CODE.detect("diagnosis: E11.9")
    assertEquals(1, matches.size)
    assertEquals("E11.9", matches.first().value)
  }

  @Test
  fun `GPS detect finds a decimal-degree coordinate pair`() {
    val matches = SensitiveElement.GPS_COORDINATES.detect("location: 48.8566, 2.3522")
    assertEquals(1, matches.size)
  }

  @Test
  fun `detect returns empty list for elements without a detection pattern`() {
    assertTrue(SensitiveElement.FULL_NAME.detect("John Doe").isEmpty())
    assertTrue(SensitiveElement.FINGERPRINT.detect("some-biometric-blob").isEmpty())
    assertTrue(SensitiveElement.HEALTH_CONDITION.detect("diabetes").isEmpty())
  }

  // ── maskValue ──────────────────────────────────────────────────────────────

  @Test
  fun `SSN maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.SOCIAL_SECURITY_NUMBER.maskValue("123-45-6789"))
  }

  @Test
  fun `credit card maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.CREDIT_CARD_NUMBER.maskValue("4111 1111 1111 1111"))
  }

  @Test
  fun `IBAN maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.IBAN.maskValue("DE89370400440532013000"))
  }

  @Test
  fun `card expiration maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.CARD_EXPIRATION_DATE.maskValue("12/25"))
  }

  @Test
  fun `health condition maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.HEALTH_CONDITION.maskValue("hypertension"))
  }

  @Test
  fun `fingerprint maskValue fully redacts the value`() {
    assertEquals("[REDACTED]", SensitiveElement.FINGERPRINT.maskValue("some-biometric-data"))
  }

  @Test
  fun `email maskValue preserves domain and masks local part`() {
    assertEquals("***@example.com", SensitiveElement.EMAIL_ADDRESS.maskValue("user@example.com"))
  }

  @Test
  fun `IP address maskValue preserves first octet and masks the rest`() {
    assertEquals("192.*.*.*", SensitiveElement.IP_ADDRESS.maskValue("192.168.1.100"))
  }

  @Test
  fun `MAC address maskValue preserves OUI and masks NIC-specific octets`() {
    assertEquals("00:1A:2B:*:*:*", SensitiveElement.MAC_ADDRESS.maskValue("00:1A:2B:3C:4D:5E"))
  }

  @Test
  fun `FULL_NAME maskValue returns the custom masking replacement without a detection pattern`() {
    assertEquals("[NAME REDACTED]", SensitiveElement.FULL_NAME.maskValue("John Doe"))
  }

  @Test
  fun `POSTAL_ADDRESS maskValue returns the custom masking replacement without a detection pattern`() {
    assertEquals("[ADDRESS REDACTED]", SensitiveElement.POSTAL_ADDRESS.maskValue("123 Main St"))
  }

  // ── maskInText ─────────────────────────────────────────────────────────────

  @Test
  fun `SSN maskInText redacts SSN embedded in a sentence`() {
    val result = SensitiveElement.SOCIAL_SECURITY_NUMBER.maskInText("My SSN is 123-45-6789.")
    assertEquals("My SSN is [REDACTED].", result)
  }

  @Test
  fun `email maskInText preserves domain while masking the local part`() {
    val result = SensitiveElement.EMAIL_ADDRESS.maskInText("Reach us at support@company.com today")
    assertEquals("Reach us at ***@company.com today", result)
  }

  @Test
  fun `email maskInText masks multiple email addresses in one string`() {
    val result = SensitiveElement.EMAIL_ADDRESS.maskInText("a@x.com and b@y.com")
    assertEquals("***@x.com and ***@y.com", result)
  }

  @Test
  fun `IP address maskInText preserves first octet in a log line`() {
    val result = SensitiveElement.IP_ADDRESS.maskInText("Request from 10.0.0.42 at 14:00")
    assertEquals("Request from 10.*.*.* at 14:00", result)
  }

  @Test
  fun `credit card maskInText fully redacts a PAN embedded in text`() {
    val result =
      SensitiveElement.CREDIT_CARD_NUMBER.maskInText(
        "charged card 4111-1111-1111-1111 successfully"
      )
    assertEquals("charged card [REDACTED] successfully", result)
  }

  @Test
  fun `maskInText returns the original text unchanged for elements without a detection pattern`() {
    val text = "John Doe, fingerprint on record"
    assertEquals(text, SensitiveElement.FULL_NAME.maskInText(text))
    assertEquals(text, SensitiveElement.FINGERPRINT.maskInText(text))
  }

  // ── metadata spot-checks ───────────────────────────────────────────────────

  @Test
  fun `SSN belongs to IDENTITY_DOCUMENT category`() {
    assertEquals(
      SensitiveCategory.IDENTITY_DOCUMENT,
      SensitiveElement.SOCIAL_SECURITY_NUMBER.category,
    )
  }

  @Test
  fun `SSN is governed by PII, GDPR, and HIPAA`() {
    val domains = SensitiveElement.SOCIAL_SECURITY_NUMBER.domains
    assertTrue(SensitivityDomain.PII in domains)
    assertTrue(SensitivityDomain.GDPR in domains)
    assertTrue(SensitivityDomain.HIPAA in domains)
  }

  @Test
  fun `CREDIT_CARD_NUMBER belongs to FINANCIAL category`() {
    assertEquals(SensitiveCategory.FINANCIAL, SensitiveElement.CREDIT_CARD_NUMBER.category)
  }

  @Test
  fun `CREDIT_CARD_NUMBER is governed by PCI_DSS`() {
    assertTrue(SensitivityDomain.PCI_DSS in SensitiveElement.CREDIT_CARD_NUMBER.domains)
  }

  @Test
  fun `FINGERPRINT is governed by GDPR, PII, and HIPAA`() {
    val domains = SensitiveElement.FINGERPRINT.domains
    assertTrue(SensitivityDomain.GDPR in domains)
    assertTrue(SensitivityDomain.PII in domains)
    assertTrue(SensitivityDomain.HIPAA in domains)
  }

  @Test
  fun `EMAIL_ADDRESS is Tier 2`() {
    assertEquals(SensitivityTier.TIER_2, SensitiveElement.EMAIL_ADDRESS.tier)
  }

  @Test
  fun `IP_ADDRESS is Tier 3`() {
    assertEquals(SensitivityTier.TIER_3, SensitiveElement.IP_ADDRESS.tier)
  }

  @Test
  fun `POSTAL_CODE is Tier 4`() {
    assertEquals(SensitivityTier.TIER_4, SensitiveElement.POSTAL_CODE.tier)
  }
}
