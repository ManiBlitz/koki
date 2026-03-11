package com.smallee.ontology

/**
 * Regulatory framework or privacy standard under which a [SensitiveElement] is classified.
 *
 * A single element may belong to multiple domains — for example, an email address is regulated by
 * both [GDPR] (EU privacy law) and [PII] (general identity-protection practice).
 */
enum class SensitivityDomain(
  val fullName: String,
  val abbreviation: String,
  val description: String,
) {
  /**
   * **General Data Protection Regulation** — EU Regulation 2016/679 governing the collection,
   * storage, and processing of personal data of EU/EEA residents. Non-compliance carries fines of
   * up to 4 % of global annual turnover or €20 million.
   */
  GDPR(
    fullName = "General Data Protection Regulation",
    abbreviation = "GDPR",
    description =
      "EU Regulation 2016/679 governing processing of personal data of EU/EEA residents. " +
        "Fines up to 4 % of global annual turnover or €20 M.",
  ),

  /**
   * **Personally Identifiable Information** — any data that can directly or indirectly identify a
   * specific living individual. Not a single law, but a cross-jurisdictional standard referenced by
   * NIST SP 800-122, CCPA, and many sector-specific regulations.
   */
  PII(
    fullName = "Personally Identifiable Information",
    abbreviation = "PII",
    description =
      "Cross-jurisdictional standard for data that can directly or indirectly identify a " +
        "specific individual (NIST SP 800-122, CCPA, and others).",
  ),

  /**
   * **Payment Card Industry Data Security Standard** — a global security standard mandated by the
   * major card brands (Visa, Mastercard, Amex, Discover, JCB) for any organisation that stores,
   * processes, or transmits cardholder data.
   */
  PCI_DSS(
    fullName = "Payment Card Industry Data Security Standard",
    abbreviation = "PCI-DSS",
    description =
      "Global security standard for organisations that store, process, or transmit " +
        "cardholder data. Mandated by Visa, Mastercard, Amex, Discover, and JCB.",
  ),

  /**
   * **Health Insurance Portability and Accountability Act** — US federal law (1996) establishing
   * national standards for the protection of individually identifiable health information
   * (Protected Health Information, PHI). Penalties range from \$100 to \$50,000 per violation.
   */
  HIPAA(
    fullName = "Health Insurance Portability and Accountability Act",
    abbreviation = "HIPAA",
    description =
      "US federal law establishing national standards for the protection of individually " +
        "identifiable health information (PHI). Penalties up to \$50,000 per violation.",
  ),
}
