package ontology

import com.smallee.ontology.SensitiveCategory
import com.smallee.ontology.SensitiveElement
import com.smallee.ontology.SensitivityDomain
import com.smallee.ontology.SensitivityTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SensitiveElementCompanionTest {

  // ── forAlias ───────────────────────────────────────────────────────────────

  @Test
  fun `forAlias resolves a canonical alias to the correct element`() {
    assertEquals(SensitiveElement.SOCIAL_SECURITY_NUMBER, SensitiveElement.forAlias("ssn"))
  }

  @Test
  fun `forAlias is case-insensitive`() {
    assertEquals(SensitiveElement.SOCIAL_SECURITY_NUMBER, SensitiveElement.forAlias("SSN"))
    assertEquals(SensitiveElement.SOCIAL_SECURITY_NUMBER, SensitiveElement.forAlias("Ssn"))
  }

  @Test
  fun `forAlias resolves camelCase alias`() {
    assertEquals(SensitiveElement.EMAIL_ADDRESS, SensitiveElement.forAlias("emailAddress"))
  }

  @Test
  fun `forAlias resolves snake_case alias`() {
    assertEquals(SensitiveElement.EMAIL_ADDRESS, SensitiveElement.forAlias("email_address"))
  }

  @Test
  fun `forAlias resolves credit card aliases`() {
    assertEquals(SensitiveElement.CREDIT_CARD_NUMBER, SensitiveElement.forAlias("pan"))
    assertEquals(SensitiveElement.CREDIT_CARD_NUMBER, SensitiveElement.forAlias("card_number"))
    assertEquals(SensitiveElement.CREDIT_CARD_NUMBER, SensitiveElement.forAlias("cardNumber"))
  }

  @Test
  fun `forAlias resolves IP address aliases`() {
    assertEquals(SensitiveElement.IP_ADDRESS, SensitiveElement.forAlias("ip_address"))
    assertEquals(SensitiveElement.IP_ADDRESS, SensitiveElement.forAlias("client_ip"))
    assertEquals(SensitiveElement.IP_ADDRESS, SensitiveElement.forAlias("x_forwarded_for"))
  }

  @Test
  fun `forAlias resolves date-of-birth aliases`() {
    assertEquals(SensitiveElement.DATE_OF_BIRTH, SensitiveElement.forAlias("dob"))
    assertEquals(SensitiveElement.DATE_OF_BIRTH, SensitiveElement.forAlias("birthday"))
  }

  @Test
  fun `forAlias returns null for an unknown field name`() {
    assertNull(SensitiveElement.forAlias("unknown_field"))
  }

  @Test
  fun `forAlias returns null for an empty string`() {
    assertNull(SensitiveElement.forAlias(""))
  }

  // ── byCategory ─────────────────────────────────────────────────────────────

  @Test
  fun `byCategory returns all financial elements`() {
    val financial = SensitiveElement.byCategory(SensitiveCategory.FINANCIAL)
    assertTrue(SensitiveElement.CREDIT_CARD_NUMBER in financial)
    assertTrue(SensitiveElement.CARD_VERIFICATION_VALUE in financial)
    assertTrue(SensitiveElement.CARD_EXPIRATION_DATE in financial)
    assertTrue(SensitiveElement.BANK_ACCOUNT_NUMBER in financial)
    assertTrue(SensitiveElement.BANK_ROUTING_NUMBER in financial)
    assertTrue(SensitiveElement.IBAN in financial)
  }

  @Test
  fun `byCategory returns all credential elements`() {
    val credentials = SensitiveElement.byCategory(SensitiveCategory.CREDENTIALS)
    assertTrue(SensitiveElement.PASSWORD in credentials)
    assertTrue(SensitiveElement.API_KEY in credentials)
    assertTrue(SensitiveElement.PRIVATE_KEY in credentials)
    assertTrue(SensitiveElement.ACCESS_TOKEN in credentials)
  }

  @Test
  fun `byCategory returns only elements belonging to the given category`() {
    val health = SensitiveElement.byCategory(SensitiveCategory.HEALTH)
    health.forEach { element ->
      assertEquals(
        SensitiveCategory.HEALTH,
        element.category,
        "${element.name} was returned by byCategory(HEALTH) but has a different category",
      )
    }
  }

  @Test
  fun `byCategory returns all biometric elements`() {
    val biometric = SensitiveElement.byCategory(SensitiveCategory.BIOMETRIC)
    assertTrue(SensitiveElement.FINGERPRINT in biometric)
    assertTrue(SensitiveElement.FACIAL_RECOGNITION_DATA in biometric)
    assertTrue(SensitiveElement.VOICE_PRINT in biometric)
  }

  @Test
  fun `byCategory returns all location elements`() {
    val location = SensitiveElement.byCategory(SensitiveCategory.LOCATION)
    assertTrue(SensitiveElement.GPS_COORDINATES in location)
    assertTrue(SensitiveElement.POSTAL_CODE in location)
    assertTrue(SensitiveElement.PRECISE_LOCATION in location)
  }

  // ── byDomain ───────────────────────────────────────────────────────────────

  @Test
  fun `byDomain returns only elements governed by the given domain`() {
    val pciElements = SensitiveElement.byDomain(SensitivityDomain.PCI_DSS)
    pciElements.forEach { element ->
      assertTrue(
        SensitivityDomain.PCI_DSS in element.domains,
        "${element.name} was returned by byDomain(PCI_DSS) but PCI_DSS is not in its domains",
      )
    }
  }

  @Test
  fun `byDomain PCI_DSS includes all payment card elements`() {
    val pciElements = SensitiveElement.byDomain(SensitivityDomain.PCI_DSS)
    assertTrue(SensitiveElement.CREDIT_CARD_NUMBER in pciElements)
    assertTrue(SensitiveElement.CARD_VERIFICATION_VALUE in pciElements)
    assertTrue(SensitiveElement.CARD_EXPIRATION_DATE in pciElements)
    assertTrue(SensitiveElement.BANK_ACCOUNT_NUMBER in pciElements)
    assertTrue(SensitiveElement.IBAN in pciElements)
  }

  @Test
  fun `byDomain HIPAA includes core PHI identifiers`() {
    val hipaaElements = SensitiveElement.byDomain(SensitivityDomain.HIPAA)
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER in hipaaElements)
    assertTrue(SensitiveElement.MEDICAL_RECORD_NUMBER in hipaaElements)
    assertTrue(SensitiveElement.DATE_OF_BIRTH in hipaaElements)
    assertTrue(SensitiveElement.FINGERPRINT in hipaaElements)
  }

  @Test
  fun `byDomain GDPR does not include purely PCI-DSS elements like CVV`() {
    val gdprElements = SensitiveElement.byDomain(SensitivityDomain.GDPR)
    assertTrue(SensitiveElement.CARD_VERIFICATION_VALUE !in gdprElements)
  }

  // ── atOrAboveTier ──────────────────────────────────────────────────────────

  @Test
  fun `atOrAboveTier TIER_0 returns only Tier 0 elements`() {
    val tier0 = SensitiveElement.atOrAboveTier(SensitivityTier.TIER_0)
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER in tier0)
    assertTrue(SensitiveElement.CREDIT_CARD_NUMBER in tier0)
    assertTrue(SensitiveElement.PASSWORD in tier0)
    assertTrue(SensitiveElement.FINGERPRINT in tier0)
    // Tier 1+ must not appear
    assertTrue(SensitiveElement.EMAIL_ADDRESS !in tier0)
    assertTrue(SensitiveElement.PASSPORT_NUMBER !in tier0)
    assertTrue(SensitiveElement.POSTAL_CODE !in tier0)
  }

  @Test
  fun `atOrAboveTier TIER_1 returns Tier 0 and Tier 1 elements`() {
    val upToTier1 = SensitiveElement.atOrAboveTier(SensitivityTier.TIER_1)
    // Tier 0 present
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER in upToTier1)
    // Tier 1 present
    assertTrue(SensitiveElement.PASSPORT_NUMBER in upToTier1)
    assertTrue(SensitiveElement.MEDICAL_RECORD_NUMBER in upToTier1)
    // Tier 2+ absent
    assertTrue(SensitiveElement.EMAIL_ADDRESS !in upToTier1)
    assertTrue(SensitiveElement.POSTAL_CODE !in upToTier1)
  }

  @Test
  fun `atOrAboveTier TIER_4 returns all elements`() {
    val all = SensitiveElement.atOrAboveTier(SensitivityTier.TIER_4)
    assertEquals(SensitiveElement.entries.size, all.size)
  }

  @Test
  fun `atOrAboveTier returns only elements whose tier level is less than or equal to the given tier level`() {
    val result = SensitiveElement.atOrAboveTier(SensitivityTier.TIER_2)
    result.forEach { element ->
      assertTrue(
        element.tier.level <= SensitivityTier.TIER_2.level,
        "${element.name} has tier level ${element.tier.level} but was returned for TIER_2",
      )
    }
  }

  // ── scanText ───────────────────────────────────────────────────────────────

  @Test
  fun `scanText detects an SSN in a log line`() {
    val results = SensitiveElement.scanText("user SSN 123-45-6789 processed")
    assertTrue(SensitiveElement.SOCIAL_SECURITY_NUMBER in results)
  }

  @Test
  fun `scanText detects multiple elements in the same string`() {
    val text = "email: user@example.com, ip: 10.0.0.1"
    val results = SensitiveElement.scanText(text)
    assertTrue(SensitiveElement.EMAIL_ADDRESS in results)
    assertTrue(SensitiveElement.IP_ADDRESS in results)
  }

  @Test
  fun `scanText returns empty map for a string with no detectable sensitive data`() {
    val results = SensitiveElement.scanText("hello world, nothing sensitive here")
    assertTrue(results.isEmpty())
  }

  @Test
  fun `scanText only includes elements with at least one match`() {
    val text = "email: user@example.com"
    val results = SensitiveElement.scanText(text)
    results.forEach { (_, matches) ->
      assertTrue(matches.isNotEmpty(), "scanText must not include elements with zero matches")
    }
  }

  @Test
  fun `scanText does not include elements without a detection pattern`() {
    val text = "John Doe, fingerprint, health condition"
    val results = SensitiveElement.scanText(text)
    assertTrue(SensitiveElement.FULL_NAME !in results)
    assertTrue(SensitiveElement.FINGERPRINT !in results)
    assertTrue(SensitiveElement.HEALTH_CONDITION !in results)
  }
}
