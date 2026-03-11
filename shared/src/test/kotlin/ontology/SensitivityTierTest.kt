package ontology

import com.smallee.ontology.SensitivityTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SensitivityTierTest {

  // ── enum structure ─────────────────────────────────────────────────────────

  @Test
  fun `enum has exactly five values`() {
    assertEquals(5, SensitivityTier.entries.size)
  }

  @Test
  fun `tiers are declared in ascending order of level`() {
    val levels = SensitivityTier.entries.map { it.level }
    assertEquals(listOf(0, 1, 2, 3, 4), levels)
  }

  // ── level values ───────────────────────────────────────────────────────────

  @Test
  fun `TIER_0 has level 0`() {
    assertEquals(0, SensitivityTier.TIER_0.level)
  }

  @Test
  fun `TIER_1 has level 1`() {
    assertEquals(1, SensitivityTier.TIER_1.level)
  }

  @Test
  fun `TIER_2 has level 2`() {
    assertEquals(2, SensitivityTier.TIER_2.level)
  }

  @Test
  fun `TIER_3 has level 3`() {
    assertEquals(3, SensitivityTier.TIER_3.level)
  }

  @Test
  fun `TIER_4 has level 4`() {
    assertEquals(4, SensitivityTier.TIER_4.level)
  }

  // ── labels ─────────────────────────────────────────────────────────────────

  @Test
  fun `TIER_0 label is Critical`() {
    assertEquals("Critical", SensitivityTier.TIER_0.label)
  }

  @Test
  fun `TIER_4 label is Minimal`() {
    assertEquals("Minimal", SensitivityTier.TIER_4.label)
  }

  // ── descriptions ───────────────────────────────────────────────────────────

  @Test
  fun `every tier has a non-blank description`() {
    SensitivityTier.entries.forEach { tier ->
      assertTrue(tier.description.isNotBlank(), "${tier.name} must have a non-blank description")
    }
  }

  // ── comparability ──────────────────────────────────────────────────────────

  @Test
  fun `higher risk tiers have a lower level number than lower risk tiers`() {
    assertTrue(SensitivityTier.TIER_0.level < SensitivityTier.TIER_4.level)
  }
}
