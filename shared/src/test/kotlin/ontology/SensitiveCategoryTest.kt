package ontology

import com.smallee.ontology.SensitiveCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SensitiveCategoryTest {

  // ── enum structure ─────────────────────────────────────────────────────────

  @Test
  fun `enum has exactly nine values`() {
    assertEquals(9, SensitiveCategory.entries.size)
  }

  // ── display names ──────────────────────────────────────────────────────────

  @Test
  fun `every category has a non-blank display name`() {
    SensitiveCategory.entries.forEach { category ->
      assertTrue(
        category.displayName.isNotBlank(),
        "${category.name} must have a non-blank displayName",
      )
    }
  }

  // ── descriptions ───────────────────────────────────────────────────────────

  @Test
  fun `every category has a non-blank description`() {
    SensitiveCategory.entries.forEach { category ->
      assertTrue(
        category.description.isNotBlank(),
        "${category.name} must have a non-blank description",
      )
    }
  }

  // ── spot-checks ────────────────────────────────────────────────────────────

  @Test
  fun `FINANCIAL displayName is Financial`() {
    assertEquals("Financial", SensitiveCategory.FINANCIAL.displayName)
  }

  @Test
  fun `BIOMETRIC displayName is Biometric`() {
    assertEquals("Biometric", SensitiveCategory.BIOMETRIC.displayName)
  }
}
