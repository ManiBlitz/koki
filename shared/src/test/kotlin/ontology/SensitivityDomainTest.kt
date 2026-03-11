package ontology

import com.smallee.ontology.SensitivityDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SensitivityDomainTest {

  // ── enum structure ─────────────────────────────────────────────────────────

  @Test
  fun `enum has exactly four values`() {
    assertEquals(4, SensitivityDomain.entries.size)
  }

  // ── abbreviations ──────────────────────────────────────────────────────────

  @Test
  fun `GDPR abbreviation is GDPR`() {
    assertEquals("GDPR", SensitivityDomain.GDPR.abbreviation)
  }

  @Test
  fun `PII abbreviation is PII`() {
    assertEquals("PII", SensitivityDomain.PII.abbreviation)
  }

  @Test
  fun `PCI_DSS abbreviation is PCI-DSS`() {
    assertEquals("PCI-DSS", SensitivityDomain.PCI_DSS.abbreviation)
  }

  @Test
  fun `HIPAA abbreviation is HIPAA`() {
    assertEquals("HIPAA", SensitivityDomain.HIPAA.abbreviation)
  }

  // ── full names ─────────────────────────────────────────────────────────────

  @Test
  fun `GDPR fullName contains General Data Protection Regulation`() {
    assertEquals("General Data Protection Regulation", SensitivityDomain.GDPR.fullName)
  }

  @Test
  fun `HIPAA fullName contains Health Insurance Portability and Accountability Act`() {
    assertEquals(
      "Health Insurance Portability and Accountability Act",
      SensitivityDomain.HIPAA.fullName,
    )
  }

  // ── descriptions ───────────────────────────────────────────────────────────

  @Test
  fun `every domain has a non-blank description`() {
    SensitivityDomain.entries.forEach { domain ->
      assertTrue(
        domain.description.isNotBlank(),
        "${domain.name} must have a non-blank description",
      )
    }
  }
}
