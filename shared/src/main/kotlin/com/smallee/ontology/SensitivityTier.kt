package com.smallee.ontology

/**
 * Classifies the sensitivity severity of a [SensitiveElement].
 *
 * Tiers are ordered from most sensitive ([TIER_0]) to least sensitive ([TIER_4]). The tier governs
 * the obfuscation strategy applied by the data-capture layer and determines the legal exposure risk
 * associated with an unmasked value.
 */
enum class SensitivityTier(val level: Int, val label: String, val description: String) {
  /**
   * **Critical** — exposure constitutes a direct regulatory violation and/or enables immediate
   * financial or identity fraud. Values must be fully redacted in all non-production contexts (e.g.
   * SSN, payment card number, biometric templates, credentials).
   */
  TIER_0(
    level = 0,
    label = "Critical",
    description =
      "Maximum sensitivity. Exposure constitutes a direct regulatory violation and enables " +
        "immediate harm. Full redaction is mandatory in all non-production contexts.",
  ),

  /**
   * **High** — exposure enables targeted identity theft, discrimination, or serious privacy breach.
   * Values must be strongly masked and access tightly controlled (e.g. passport number, medical
   * record number, health conditions).
   */
  TIER_1(
    level = 1,
    label = "High",
    description =
      "High sensitivity. Exposure enables targeted identity theft or serious privacy breach. " +
        "Strong masking is required and data access must be tightly controlled.",
  ),

  /**
   * **Medium** — exposure allows profiling or targeted attacks when combined with other data.
   * Values should be partially masked at rest and in transit (e.g. full name, email address, phone
   * number, date of birth).
   */
  TIER_2(
    level = 2,
    label = "Medium",
    description =
      "Moderate sensitivity. Exposure enables profiling or targeted attacks when combined " +
        "with other data. Partial masking is required.",
  ),

  /**
   * **Low** — exposure alone poses a limited direct threat but contributes meaningfully to
   * re-identification when aggregated with other attributes (e.g. IP address, device ID, precise
   * geographic location).
   */
  TIER_3(
    level = 3,
    label = "Low",
    description =
      "Low sensitivity. Exposure contributes to re-identification when aggregated. " +
        "Minimal obfuscation is required.",
  ),

  /**
   * **Minimal** — exposure alone carries negligible individual risk. Context-dependent obfuscation
   * may still be required under certain regulatory regimes (e.g. age range, gender, postal code,
   * nationality).
   */
  TIER_4(
    level = 4,
    label = "Minimal",
    description =
      "Minimal sensitivity. Exposure alone poses limited individual risk. " +
        "Context-dependent obfuscation may still apply under certain regulations.",
  ),
}
