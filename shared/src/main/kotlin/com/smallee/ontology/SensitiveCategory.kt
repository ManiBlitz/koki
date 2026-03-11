package com.smallee.ontology

/**
 * High-level logical grouping for [SensitiveElement] entries.
 *
 * Each [SensitiveElement] references exactly one [SensitiveCategory] as its semantic parent,
 * enabling hierarchical traversal of the sensitivity taxonomy (e.g. "list all health-related
 * sensitive attributes" or "which financial identifiers are captured?").
 */
enum class SensitiveCategory(val displayName: String, val description: String) {
  /** Government-issued documents and unique numeric identifiers assigned to individuals. */
  IDENTITY_DOCUMENT(
    displayName = "Identity Document",
    description =
      "Government-issued documents and unique numeric identifiers assigned to individuals " +
        "(e.g. SSN, passport, driver's licence, national ID, tax ID).",
  ),

  /** Payment instruments, account numbers, and financial data subject to PCI-DSS controls. */
  FINANCIAL(
    displayName = "Financial",
    description =
      "Payment instruments, bank account numbers, and financial identifiers subject to " +
        "PCI-DSS and financial privacy regulations (e.g. credit card, IBAN, routing number).",
  ),

  /**
   * Secrets used to authenticate or authorise access to systems, APIs, or accounts. Compromise of
   * these elements grants direct system access.
   */
  CREDENTIALS(
    displayName = "Credentials",
    description =
      "Secrets used to authenticate or authorise access to systems, APIs, or accounts " +
        "(e.g. passwords, API keys, private keys, access tokens).",
  ),

  /** Direct contact details that allow reaching or locating a specific individual. */
  CONTACT_INFO(
    displayName = "Contact Information",
    description =
      "Direct contact details that allow reaching or locating a specific individual " +
        "(e.g. full name, email address, phone number, postal address).",
  ),

  /**
   * Unique physical or behavioural characteristics used to identify individuals. Biometric data is
   * a special category under GDPR Article 9 requiring explicit consent.
   */
  BIOMETRIC(
    displayName = "Biometric",
    description =
      "Unique physical or behavioural characteristics used to identify individuals " +
        "(e.g. fingerprint, facial recognition data, voice print). GDPR Article 9 special category.",
  ),

  /**
   * Medical history, clinical data, and health-care identifiers protected under HIPAA and
   * classified as a special category under GDPR Article 9.
   */
  HEALTH(
    displayName = "Health",
    description =
      "Medical history, clinical data, and health-care identifiers protected under HIPAA " +
        "and GDPR Article 9 special categories (e.g. MRN, diagnosis codes, conditions).",
  ),

  /** Demographic attributes that characterise an individual within a population. */
  DEMOGRAPHIC(
    displayName = "Demographic",
    description =
      "Demographic attributes characterising an individual within a population " +
        "(e.g. date of birth, age, gender, ethnicity, nationality).",
  ),

  /**
   * Digital identifiers assigned to devices, sessions, or network endpoints. Under GDPR, IP
   * addresses and persistent identifiers are considered personal data.
   */
  NETWORK_IDENTIFIER(
    displayName = "Network Identifier",
    description =
      "Digital identifiers assigned to devices, sessions, or network endpoints. IP addresses " +
        "and persistent device IDs are personal data under GDPR " +
        "(e.g. IP address, MAC address, device ID, session ID, cookie ID).",
  ),

  /** Physical or geographic position data that can reveal an individual's movements or home. */
  LOCATION(
    displayName = "Location",
    description =
      "Physical or geographic position data that can reveal an individual's movements or home " +
        "(e.g. GPS coordinates, precise location, postal code).",
  ),
}
