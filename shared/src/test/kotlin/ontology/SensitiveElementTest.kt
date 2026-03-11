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
  fun `passport detect finds an uppercase prefix`() {
    assertEquals(1, SensitiveElement.PASSPORT_NUMBER.detect("passport: A12345678").size)
  }

  @Test
  fun `passport detect finds a lowercase prefix`() {
    assertEquals(1, SensitiveElement.PASSPORT_NUMBER.detect("passport: a12345678").size)
  }

  @Test
  fun `passport detect finds a two-letter uppercase prefix`() {
    assertEquals(1, SensitiveElement.PASSPORT_NUMBER.detect("travel doc AB1234567").size)
  }

  @Test
  fun `passport detect finds a two-letter lowercase prefix`() {
    assertEquals(1, SensitiveElement.PASSPORT_NUMBER.detect("travel doc ab1234567").size)
  }

  @Test
  fun `IBAN detect finds a valid IBAN`() {
    val matches = SensitiveElement.IBAN.detect("IBAN: DE89370400440532013000")
    assertEquals(1, matches.size)
    assertEquals("DE89370400440532013000", matches.first().value)
  }

  // ── PASSWORD detection — quoted and unquoted values ───────────────────────

  @Test
  fun `password detect matches an unquoted value`() {
    assertEquals(1, SensitiveElement.PASSWORD.detect("password=secret123").size)
  }

  @Test
  fun `password detect matches a double-quoted passphrase containing spaces`() {
    val matches = SensitiveElement.PASSWORD.detect("""password = "my secret phrase"""")
    assertEquals(1, matches.size)
    assertTrue(matches.first().value.contains("my secret phrase"))
  }

  @Test
  fun `password detect matches a single-quoted passphrase containing spaces`() {
    val matches = SensitiveElement.PASSWORD.detect("passphrase = 'correct horse battery'")
    assertEquals(1, matches.size)
    assertTrue(matches.first().value.contains("correct horse battery"))
  }

  @Test
  fun `password detect matches case-insensitively for the label`() {
    assertEquals(1, SensitiveElement.PASSWORD.detect("Password: hunter2").size)
    assertEquals(1, SensitiveElement.PASSWORD.detect("PWD=abc").size)
  }

  @Test
  fun `password detect does not match a bare word without a label`() {
    assertTrue(SensitiveElement.PASSWORD.detect("hunter2").isEmpty())
  }

  @Test
  fun `CVV detect matches cvv label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cvv=123").size)
  }

  @Test
  fun `CVV detect matches cvc2 label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cvc2: 456").size)
  }

  @Test
  fun `CVV detect matches cid label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cid=1234").size)
  }

  @Test
  fun `CVV detect matches security_code label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("security_code=321").size)
  }

  @Test
  fun `CVV detect matches securityCode camelCase label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("securityCode: 789").size)
  }

  @Test
  fun `CVV detect matches card_security_code label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("card_security_code=555").size)
  }

  @Test
  fun `CVV detect matches cardSecurityCode camelCase label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cardSecurityCode=999").size)
  }

  @Test
  fun `CVV detect matches cvv_code label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cvv_code=123").size)
  }

  @Test
  fun `CVV detect matches cvvCode camelCase label`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("cvvCode: 456").size)
  }

  @Test
  fun `CVV detect is case-insensitive`() {
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("CVV=123").size)
    assertEquals(1, SensitiveElement.CARD_VERIFICATION_VALUE.detect("Security_Code=321").size)
  }

  @Test
  fun `CVV detect does not match a bare 3-digit number without a label`() {
    assertTrue(SensitiveElement.CARD_VERIFICATION_VALUE.detect("amount is 123").isEmpty())
  }

  // ── PRIVATE_KEY detection covers the full PEM block ────────────────────────

  @Test
  fun `private key detect matches the full PEM block including body and END footer`() {
    val pem =
      """
      -----BEGIN RSA PRIVATE KEY-----
      MIIEowIBAAKCAQEA0Z3VS5JJcds3xHn/ygWep4/sBXMZPiQABFEZdSPqSBzHKTeN
      aqHyLRfVSMqCsq3UJKTYM+tN5qlqiNbZ9v7mjHnOVvGrHgSIbKK5ljKGbXDpFDYH
      -----END RSA PRIVATE KEY-----
      """
        .trimIndent()
    val matches = SensitiveElement.PRIVATE_KEY.detect(pem)
    assertEquals(1, matches.size)
  }

  @Test
  fun `private key detect match value includes the body and END footer not just the header`() {
    val pem =
      """
      -----BEGIN EC PRIVATE KEY-----
      MHQCAQEEIOk3NHMK7lsE9BNgCivCVg9RdMEpuHETOBj+sGlQGgZ9oAoGCCqGSM49
      AwEHoWQDYgAE
      -----END EC PRIVATE KEY-----
      """
        .trimIndent()
    val match = SensitiveElement.PRIVATE_KEY.detect(pem).first().value
    assertTrue(match.contains("-----END EC PRIVATE KEY-----"), "Match must include the END footer")
    assertTrue(match.contains("MHQCAQEEIOk3"), "Match must include the base64 body")
  }

  @Test
  fun `private key detect handles an untyped PRIVATE KEY block`() {
    val pem = "-----BEGIN PRIVATE KEY-----\nMIIEvQ==\n-----END PRIVATE KEY-----"
    assertEquals(1, SensitiveElement.PRIVATE_KEY.detect(pem).size)
  }

  @Test
  fun `private key maskInText redacts the entire PEM block leaving surrounding text intact`() {
    val input = "key: -----BEGIN RSA PRIVATE KEY-----\nABCD==\n-----END RSA PRIVATE KEY-----\nend"
    val result = SensitiveElement.PRIVATE_KEY.maskInText(input)
    assertEquals("key: [REDACTED]\nend", result)
  }

  @Test
  fun `private key detect finds two separate PEM blocks independently`() {
    val input =
      """
      -----BEGIN RSA PRIVATE KEY-----
      AAAA==
      -----END RSA PRIVATE KEY-----
      some text
      -----BEGIN EC PRIVATE KEY-----
      BBBB==
      -----END EC PRIVATE KEY-----
      """
        .trimIndent()
    assertEquals(2, SensitiveElement.PRIVATE_KEY.detect(input).size)
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

  // ── DATE_OF_SERVICE vs DATE_OF_BIRTH disambiguation ───────────────────────

  @Test
  fun `date of service detect matches when a known label precedes the date`() {
    assertEquals(1, SensitiveElement.DATE_OF_SERVICE.detect("service_date: 2026-03-11").size)
    assertEquals(1, SensitiveElement.DATE_OF_SERVICE.detect("dateOfService=2024-07-04").size)
    assertEquals(1, SensitiveElement.DATE_OF_SERVICE.detect("dos: 01/15/2025").size)
    assertEquals(1, SensitiveElement.DATE_OF_SERVICE.detect("encounter_date=2023-12-01").size)
  }

  @Test
  fun `date of service detect does not match a bare date without a label`() {
    assertTrue(SensitiveElement.DATE_OF_SERVICE.detect("2026-03-11").isEmpty())
    assertTrue(SensitiveElement.DATE_OF_SERVICE.detect("admitted on 2026-03-11").isEmpty())
  }

  @Test
  fun `date of birth detect still matches a bare unlabeled date`() {
    assertEquals(1, SensitiveElement.DATE_OF_BIRTH.detect("1990-06-15").size)
    assertEquals(1, SensitiveElement.DATE_OF_BIRTH.detect("patient born 15/06/1990").size)
  }

  @Test
  fun `a bare date matches DATE_OF_BIRTH but not DATE_OF_SERVICE via scanText`() {
    val results = SensitiveElement.scanText("dob: 1985-04-23")
    assertTrue(SensitiveElement.DATE_OF_BIRTH in results)
    assertTrue(SensitiveElement.DATE_OF_SERVICE !in results)
  }

  @Test
  fun `a labeled service date matches DATE_OF_SERVICE via scanText`() {
    // The date portion will also trigger DATE_OF_BIRTH's generic pattern — that is expected.
    // The key guarantee is that DATE_OF_SERVICE fires when its label is present.
    val results = SensitiveElement.scanText("service_date: 2026-03-11")
    assertTrue(SensitiveElement.DATE_OF_SERVICE in results)
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

  @Test
  fun `maskValue returns maskingReplacement when detectionPattern is non-null but finds no match`() {
    // EMAIL_ADDRESS has a detectionPattern; "not-an-email" does not match it.
    // The original value must not be returned — maskingReplacement must come back instead.
    val element = SensitiveElement.EMAIL_ADDRESS
    assertEquals(element.maskingReplacement, element.maskValue("not-an-email"))
  }

  @Test
  fun `maskValue does not leak the original value when the pattern does not match`() {
    // SSN pattern requires exactly 9 digits in the right grouping; a random number must not pass
    // through
    val result = SensitiveElement.SOCIAL_SECURITY_NUMBER.maskValue("12345")
    assertEquals("[REDACTED]", result)
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
