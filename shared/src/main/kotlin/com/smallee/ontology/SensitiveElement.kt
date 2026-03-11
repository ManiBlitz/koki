package com.smallee.ontology

/** Sentinel replacement string for values that must be fully redacted. */
private const val FULL_REDACTION = "[REDACTED]"

/**
 * A comprehensive catalogue of sensitive data elements spanning GDPR, PII, PCI-DSS, and HIPAA
 * regulatory frameworks.
 *
 * Each entry describes a single logical sensitive attribute and carries everything needed for
 * automated detection and masking at the data-capture layer:
 * - [displayName] / [shortName] — human-readable identifiers
 * - [aliases] — canonical field names across common API, database, and form conventions
 *   (snake_case, camelCase, abbreviations) used for attribute-name matching
 * - [category] — the logical parent [SensitiveCategory] grouping this element
 * - [domains] — the set of [SensitivityDomain] regulations that govern this element
 * - [tier] — the [SensitivityTier] controlling the default obfuscation strategy
 * - [detectionPattern] — a [Regex] for locating this value in free-form text (`null` when
 *   pattern-based detection is unreliable and NLP / dictionary lookup is required)
 * - [maskingReplacement] — the replacement string applied via [Regex.replace] against
 *   [detectionPattern]. [SensitivityTier.TIER_0] and [SensitivityTier.TIER_1] elements always use
 *   the default `"[REDACTED]"` (full redaction). Lower tiers may use backreferences (`$1`, `$2`, …)
 *   to preserve safe partial values (e.g. email domain, first IP octet).
 *
 * ### Masking helpers
 * - [maskInText] — replaces all detected occurrences within a larger string
 * - [maskValue] — masks a standalone known-sensitive value
 * - [detect] — returns all [MatchResult]s for this element's pattern in a given string
 *
 * ### Companion helpers
 * - [scanText] — returns every element that has detectable matches in a string
 * - [forAlias] — looks up an element by any of its known aliases
 *
 * @see SensitiveCategory
 * @see SensitivityDomain
 * @see SensitivityTier
 */
@Suppress("LongParameterList")
enum class SensitiveElement(
  val displayName: String,
  val shortName: String,
  val aliases: List<String>,
  val category: SensitiveCategory,
  val domains: Set<SensitivityDomain>,
  val tier: SensitivityTier,
  val detectionPattern: Regex?,
  val maskingReplacement: String = FULL_REDACTION,
) {

  // ---------------------------------------------------------------------------
  // IDENTITY DOCUMENTS
  // ---------------------------------------------------------------------------

  /**
   * **Social Security Number** — the primary US national identifier, also applicable to Canada's
   * SIN. HIPAA lists SSN as one of its 18 PHI identifiers.
   *
   * Detection reveals the last four digits; the first five are masked.
   */
  SOCIAL_SECURITY_NUMBER(
    displayName = "Social Security Number",
    shortName = "SSN",
    aliases =
      listOf(
        "ssn",
        "social_security_number",
        "socialSecurityNumber",
        "social-security-number",
        "social_security",
        "us_ssn",
        "sin", // Canada: Social Insurance Number
        "taxpayer_id_us",
      ),
    category = SensitiveCategory.IDENTITY_DOCUMENT,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR, SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_0,
    detectionPattern = Regex("""\b\d{3}[- ]?\d{2}[- ]?\d{4}\b"""),
  ),

  /**
   * **Passport Number** — government-issued travel document identifier. Regex covers common
   * alphanumeric formats (e.g. US: A12345678, UK: 123456789).
   */
  PASSPORT_NUMBER(
    displayName = "Passport Number",
    shortName = "PASS",
    aliases =
      listOf(
        "passport",
        "passport_number",
        "passportNumber",
        "passport_no",
        "passportNo",
        "travel_document_number",
        "travelDocumentNumber",
      ),
    category = SensitiveCategory.IDENTITY_DOCUMENT,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_1,
    detectionPattern = Regex("""\b[A-Z]{1,2}[0-9]{6,9}\b"""),
  ),

  /**
   * **Driver's Licence Number** — format is highly country- and state-specific; regex detection is
   * unreliable without jurisdiction context. Falls back to alias-based matching.
   */
  DRIVERS_LICENSE_NUMBER(
    displayName = "Driver's Licence Number",
    shortName = "DLN",
    aliases =
      listOf(
        "drivers_license",
        "driversLicense",
        "driver_license",
        "driverLicense",
        "driving_license",
        "drivingLicense",
        "dl",
        "dl_number",
        "dlNumber",
        "license_number",
        "licenseNumber",
        "licence_number",
        "licenceNumber",
      ),
    category = SensitiveCategory.IDENTITY_DOCUMENT,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_1,
    detectionPattern = null, // too jurisdiction-specific for a reliable universal regex
  ),

  /**
   * **National ID Number** — government-issued identity card number. Format varies widely by
   * country (e.g. French CNI, German Personalausweis, South African ID number).
   */
  NATIONAL_ID_NUMBER(
    displayName = "National ID Number",
    shortName = "NID",
    aliases =
      listOf(
        "national_id",
        "nationalId",
        "national_id_number",
        "nationalIdNumber",
        "identity_card_number",
        "identityCardNumber",
        "id_card_number",
        "idCardNumber",
        "citizen_id",
        "citizenId",
        "nid",
      ),
    category = SensitiveCategory.IDENTITY_DOCUMENT,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_1,
    detectionPattern = null,
  ),

  /**
   * **Tax Identification Number** — includes US EIN (XX-XXXXXXX), ITIN, and equivalent identifiers
   * in other jurisdictions.
   */
  TAX_IDENTIFICATION_NUMBER(
    displayName = "Tax Identification Number",
    shortName = "TIN",
    aliases =
      listOf(
        "tin",
        "tax_id",
        "taxId",
        "tax_identification_number",
        "taxIdentificationNumber",
        "ein",
        "employer_identification_number",
        "employerIdentificationNumber",
        "itin",
        "vat_number",
        "vatNumber",
        "tax_number",
        "taxNumber",
      ),
    category = SensitiveCategory.IDENTITY_DOCUMENT,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_1,
    // US EIN format: XX-XXXXXXX
    detectionPattern = Regex("""\b\d{2}-?\d{7}\b"""),
  ),

  // ---------------------------------------------------------------------------
  // FINANCIAL
  // ---------------------------------------------------------------------------

  /**
   * **Credit / Debit Card Number (PAN)** — covers 16-digit card formats (Visa, Mastercard,
   * Discover). The last four digits are preserved; the rest are masked.
   *
   * Note: Amex (15 digits: XXXX-XXXXXX-XXXXX) requires a separate pattern; this regex targets the
   * most common 16-digit format only.
   */
  CREDIT_CARD_NUMBER(
    displayName = "Credit / Debit Card Number",
    shortName = "CCN",
    aliases =
      listOf(
        "credit_card",
        "creditCard",
        "credit_card_number",
        "creditCardNumber",
        "card_number",
        "cardNumber",
        "pan",
        "payment_card_number",
        "paymentCardNumber",
        "cc_number",
        "ccNumber",
        "debit_card_number",
        "debitCardNumber",
      ),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_0,
    detectionPattern = Regex("""\b\d{4}[- ]?\d{4}[- ]?\d{4}[- ]?\d{4}\b"""),
  ),

  /**
   * **Card Verification Value** — the 3- or 4-digit security code on payment cards (CVV2, CVC2,
   * CID). PCI-DSS §3.2 prohibits storing this value after authorisation.
   */
  CARD_VERIFICATION_VALUE(
    displayName = "Card Verification Value",
    shortName = "CVV",
    aliases =
      listOf(
        "cvv",
        "cvc",
        "cvv2",
        "cvc2",
        "cid",
        "card_security_code",
        "cardSecurityCode",
        "security_code",
        "securityCode",
        "cvv_code",
        "cvvCode",
      ),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS),
    tier = SensitivityTier.TIER_0,
    // Alternation ordered most-specific first to avoid partial label shadowing
    detectionPattern =
      Regex(
        """(?i)\b(?:card[_\s]?security[_\s]?code|cvv[_\s]?code|security[_\s]?code|cv[cv]2?|cid)\s*[:=]\s*(\d{3,4})\b"""
      ),
  ),

  /**
   * **Card Expiration Date** — the month/year validity window of a payment card. The year component
   * is preserved; the month is masked.
   */
  CARD_EXPIRATION_DATE(
    displayName = "Card Expiration Date",
    shortName = "CED",
    aliases =
      listOf(
        "expiry",
        "expiry_date",
        "expiryDate",
        "expiration_date",
        "expirationDate",
        "card_expiry",
        "cardExpiry",
        "exp_date",
        "expDate",
        "valid_thru",
        "validThru",
        "card_expiration",
        "cardExpiration",
      ),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS),
    tier = SensitivityTier.TIER_1,
    detectionPattern = Regex("""\b(?:0[1-9]|1[0-2])\s*[\/\-]\s*(?:\d{2}|\d{4})\b"""),
  ),

  /**
   * **Bank Account Number** — format varies significantly across countries and banking systems.
   * Alias-based matching is preferred over regex detection.
   */
  BANK_ACCOUNT_NUMBER(
    displayName = "Bank Account Number",
    shortName = "BAN",
    aliases =
      listOf(
        "account_number",
        "accountNumber",
        "bank_account",
        "bankAccount",
        "bank_account_number",
        "bankAccountNumber",
        "acct_no",
        "acctNo",
        "account_no",
        "accountNo",
      ),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS, SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_0,
    detectionPattern = null,
  ),

  /**
   * **Bank Routing Number** — a 9-digit ABA transit number that identifies a US financial
   * institution. Alone it does not enable fraud, but combined with an account number it does.
   */
  BANK_ROUTING_NUMBER(
    displayName = "Bank Routing Number",
    shortName = "BRN",
    aliases =
      listOf(
        "routing_number",
        "routingNumber",
        "routing",
        "aba_number",
        "abaNumber",
        "aba_routing_number",
        "abaRoutingNumber",
        "transit_number",
        "transitNumber",
        "sort_code", // UK equivalent
        "sortCode",
      ),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_1,
    detectionPattern =
      null, // 9-digit ABA numbers are indistinguishable from other numeric IDs without context
  ),

  /**
   * **International Bank Account Number (IBAN)** — up to 34 alphanumeric characters. The country
   * code and check digits are preserved to aid debugging.
   */
  IBAN(
    displayName = "International Bank Account Number",
    shortName = "IBAN",
    aliases = listOf("iban", "international_bank_account_number", "internationalBankAccountNumber"),
    category = SensitiveCategory.FINANCIAL,
    domains = setOf(SensitivityDomain.PCI_DSS, SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_0,
    detectionPattern = Regex("""\b[A-Z]{2}\d{2}[A-Z0-9]{1,30}\b"""),
  ),

  // ---------------------------------------------------------------------------
  // CREDENTIALS
  // ---------------------------------------------------------------------------

  /**
   * **Password / Passphrase** — detected when a password appears as a key-value pair in logs,
   * configuration files, or request bodies. The entire key-value expression is redacted.
   */
  PASSWORD(
    displayName = "Password",
    shortName = "PWD",
    aliases =
      listOf(
        "password",
        "passwd",
        "pwd",
        "pass",
        "passphrase",
        "pin",
        "passcode",
        "credential",
        "secret",
        "user_password",
        "userPassword",
      ),
    category = SensitiveCategory.CREDENTIALS,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_0,
    detectionPattern =
      Regex("""(?i)(?:password|passwd|pwd|pass|passphrase|pin|passcode)\s*[:=]\s*\S+"""),
  ),

  /**
   * **API Key / Secret Key** — long random strings used to authenticate programmatic access.
   * Detected when the key label appears adjacent to its value.
   */
  API_KEY(
    displayName = "API Key",
    shortName = "APIKEY",
    aliases =
      listOf(
        "api_key",
        "apiKey",
        "x_api_key",
        "xApiKey",
        "api_token",
        "apiToken",
        "secret_key",
        "secretKey",
        "access_key",
        "accessKey",
        "client_secret",
        "clientSecret",
        "app_secret",
        "appSecret",
      ),
    category = SensitiveCategory.CREDENTIALS,
    domains = setOf(SensitivityDomain.PII),
    tier = SensitivityTier.TIER_0,
    detectionPattern =
      Regex(
        """(?i)(?:api[_-]?key|apikey|secret[_-]?key|access[_-]?key|client[_-]?secret|app[_-]?secret)\s*[:=]\s*[A-Za-z0-9\-._~+\/=]{20,}"""
      ),
  ),

  /**
   * **Private Cryptographic Key** — PEM-encoded private keys (RSA, EC, DSA, OpenSSH). The detection
   * pattern matches the full PEM block — BEGIN header, base64 body, and END footer — using
   * DOT_MATCHES_ALL (`(?s)`) so that `maskInText` redacts the entire key, not just the header line.
   */
  PRIVATE_KEY(
    displayName = "Private Cryptographic Key",
    shortName = "PKEY",
    aliases =
      listOf(
        "private_key",
        "privateKey",
        "rsa_key",
        "rsaKey",
        "pem_key",
        "pemKey",
        "signing_key",
        "signingKey",
        "tls_key",
        "tlsKey",
      ),
    category = SensitiveCategory.CREDENTIALS,
    domains = setOf(SensitivityDomain.PII),
    tier = SensitivityTier.TIER_0,
    // (?s) enables DOT_MATCHES_ALL so '.' spans newlines across the base64 body.
    // Lazy .+? stops at the first matching END marker, handling multiple keys in one string.
    detectionPattern =
      Regex(
        """(?s)-----BEGIN (?:RSA |EC |DSA |OPENSSH )?PRIVATE KEY-----.+?-----END (?:RSA |EC |DSA |OPENSSH )?PRIVATE KEY-----"""
      ),
  ),

  /**
   * **Access / Bearer Token** — short-lived tokens (JWT, OAuth 2.0 Bearer, session tokens) that
   * grant access to protected resources. Detected when the token label precedes the value.
   */
  ACCESS_TOKEN(
    displayName = "Access Token",
    shortName = "ATOKEN",
    aliases =
      listOf(
        "access_token",
        "accessToken",
        "bearer_token",
        "bearerToken",
        "auth_token",
        "authToken",
        "jwt",
        "id_token",
        "idToken",
        "refresh_token",
        "refreshToken",
        "oauth_token",
        "oauthToken",
        "session_token",
        "sessionToken",
      ),
    category = SensitiveCategory.CREDENTIALS,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_0,
    detectionPattern =
      Regex(
        """(?i)(?:access[_-]?token|bearer|auth[_-]?token|id[_-]?token|refresh[_-]?token|oauth[_-]?token)\s*[:=]\s*[A-Za-z0-9\-._~+\/=]{20,}"""
      ),
  ),

  // ---------------------------------------------------------------------------
  // CONTACT INFORMATION
  // ---------------------------------------------------------------------------

  /**
   * **Full Name** — any combination of given name, middle name, and family name. Reliable
   * regex-based detection requires NLP; detection is alias-based only.
   */
  FULL_NAME(
    displayName = "Full Name",
    shortName = "NAME",
    aliases =
      listOf(
        "full_name",
        "fullName",
        "name",
        "first_name",
        "firstName",
        "last_name",
        "lastName",
        "given_name",
        "givenName",
        "family_name",
        "familyName",
        "middle_name",
        "middleName",
        "display_name",
        "displayName",
        "legal_name",
        "legalName",
      ),
    category = SensitiveCategory.CONTACT_INFO,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    detectionPattern = null, // requires NLP / named-entity recognition
    maskingReplacement = "[NAME REDACTED]",
  ),

  /**
   * **Email Address** — the domain is preserved to aid deliverability debugging; the local part is
   * fully masked.
   */
  EMAIL_ADDRESS(
    displayName = "Email Address",
    shortName = "EMAIL",
    aliases =
      listOf(
        "email",
        "email_address",
        "emailAddress",
        "e_mail",
        "mail",
        "contact_email",
        "contactEmail",
        "user_email",
        "userEmail",
        "from_email",
        "fromEmail",
      ),
    category = SensitiveCategory.CONTACT_INFO,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    // Groups: (1) local part (masked), (2) domain (revealed)
    detectionPattern = Regex("""\b([a-zA-Z0-9._%+\-]+)@([a-zA-Z0-9.\-]+\.[a-zA-Z]{2,})\b"""),
    maskingReplacement = "***@$2",
  ),

  /**
   * **Phone Number** — broad international format detection covering North American and many
   * European formats. Fully redacted due to variable structure.
   */
  PHONE_NUMBER(
    displayName = "Phone Number",
    shortName = "PHONE",
    aliases =
      listOf(
        "phone",
        "phone_number",
        "phoneNumber",
        "telephone",
        "tel",
        "mobile",
        "mobile_number",
        "mobileNumber",
        "cell",
        "cell_phone",
        "cellPhone",
        "contact_number",
        "contactNumber",
        "fax",
        "fax_number",
        "faxNumber",
      ),
    category = SensitiveCategory.CONTACT_INFO,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    detectionPattern = Regex("""(?:\+?[1-9]\d{0,2}[-.\s]?)?\(?\d{3}\)?[-.\s]\d{3}[-.\s]\d{4}\b"""),
    maskingReplacement = "[PHONE REDACTED]",
  ),

  /**
   * **Postal / Mailing Address** — requires NLP or structured geocoding for reliable detection.
   * Alias-based matching is the primary detection mechanism.
   */
  POSTAL_ADDRESS(
    displayName = "Postal Address",
    shortName = "ADDR",
    aliases =
      listOf(
        "address",
        "street_address",
        "streetAddress",
        "home_address",
        "homeAddress",
        "mailing_address",
        "mailingAddress",
        "postal_address",
        "postalAddress",
        "billing_address",
        "billingAddress",
        "shipping_address",
        "shippingAddress",
        "residential_address",
        "residentialAddress",
      ),
    category = SensitiveCategory.CONTACT_INFO,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    detectionPattern = null,
    maskingReplacement = "[ADDRESS REDACTED]",
  ),

  // ---------------------------------------------------------------------------
  // BIOMETRIC
  // ---------------------------------------------------------------------------

  /**
   * **Fingerprint** — GDPR Article 9 special category. Raw minutiae data or template hashes cannot
   * be detected by pattern; alias-based matching is authoritative.
   */
  FINGERPRINT(
    displayName = "Fingerprint",
    shortName = "FP",
    aliases =
      listOf(
        "fingerprint",
        "finger_print",
        "finger_scan",
        "fingerprint_data",
        "fingerprintData",
        "biometric_fingerprint",
        "biometricFingerprint",
      ),
    category = SensitiveCategory.BIOMETRIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR, SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_0,
    detectionPattern = null,
  ),

  /**
   * **Facial Recognition Data** — embeddings, feature vectors, or imagery used by facial
   * recognition systems. GDPR Article 9 special category when processed for identification.
   */
  FACIAL_RECOGNITION_DATA(
    displayName = "Facial Recognition Data",
    shortName = "FACE",
    aliases =
      listOf(
        "facial_data",
        "facialData",
        "face_id",
        "faceId",
        "facial_features",
        "facialFeatures",
        "face_scan",
        "faceScan",
        "face_recognition",
        "faceRecognition",
        "face_embedding",
        "faceEmbedding",
      ),
    category = SensitiveCategory.BIOMETRIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR, SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_0,
    detectionPattern = null,
  ),

  /**
   * **Voice Print** — acoustic or spectral models derived from speech recordings used to identify a
   * speaker. GDPR Article 9 special category.
   */
  VOICE_PRINT(
    displayName = "Voice Print",
    shortName = "VP",
    aliases =
      listOf(
        "voice_print",
        "voicePrint",
        "voice_id",
        "voiceId",
        "voice_data",
        "voiceData",
        "voice_biometric",
        "voiceBiometric",
        "speaker_id",
        "speakerId",
      ),
    category = SensitiveCategory.BIOMETRIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR, SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_0,
    detectionPattern = null,
  ),

  // ---------------------------------------------------------------------------
  // HEALTH
  // ---------------------------------------------------------------------------

  /**
   * **Medical Record Number (MRN)** — HIPAA §164.514(b) lists MRN as a PHI identifier. Detected
   * when the MRN label appears adjacent to an alphanumeric token.
   */
  MEDICAL_RECORD_NUMBER(
    displayName = "Medical Record Number",
    shortName = "MRN",
    aliases =
      listOf(
        "mrn",
        "medical_record_number",
        "medicalRecordNumber",
        "patient_id",
        "patientId",
        "medical_id",
        "medicalId",
        "emr_id",
        "emrId",
        "ehr_id",
        "ehrId",
        "chart_number",
        "chartNumber",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA, SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_1,
    detectionPattern =
      Regex(
        """(?i)(?:mrn|medical[_-]?record(?:[_-]?(?:number|no|num|id))?|patient[_-]?id)\s*[=:#\/]\s*([A-Z0-9\-]{4,15})"""
      ),
  ),

  /**
   * **Health Insurance Beneficiary Number** — plan member ID or policy number used to identify an
   * insured individual. Listed as a HIPAA PHI identifier.
   */
  HEALTH_INSURANCE_BENEFICIARY_NUMBER(
    displayName = "Health Insurance Beneficiary Number",
    shortName = "HIBN",
    aliases =
      listOf(
        "insurance_id",
        "insuranceId",
        "member_id",
        "memberId",
        "policy_number",
        "policyNumber",
        "insurance_member_id",
        "insuranceMemberId",
        "health_plan_id",
        "healthPlanId",
        "subscriber_id",
        "subscriberId",
        "beneficiary_id",
        "beneficiaryId",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_1,
    detectionPattern = null,
  ),

  /**
   * **Diagnosis Code (ICD-10)** — a clinical classification code describing a patient's condition
   * (e.g. `E11.9` for type 2 diabetes without complications).
   *
   * The regex matches the ICD-10 format; ICD-9 codes (3–5 digit numeric) may produce false
   * positives and require contextual disambiguation.
   */
  DIAGNOSIS_CODE(
    displayName = "Diagnosis Code",
    shortName = "DXC",
    aliases =
      listOf(
        "diagnosis_code",
        "diagnosisCode",
        "icd_code",
        "icdCode",
        "icd10",
        "icd_10",
        "icd_9",
        "dx_code",
        "dxCode",
        "clinical_code",
        "clinicalCode",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    // ICD-10: letter + 2 digits + optional decimal sub-classification
    detectionPattern = Regex("""\b[A-Z]\d{2}(?:\.\d{1,4})?\b"""),
    maskingReplacement = "[DIAGNOSIS REDACTED]",
  ),

  /**
   * **Medication Name** — prescription drug or over-the-counter medication. Requires a
   * pharmaceutical dictionary (e.g. RxNorm) for reliable detection.
   */
  MEDICATION_NAME(
    displayName = "Medication Name",
    shortName = "MED",
    aliases =
      listOf(
        "medication",
        "medication_name",
        "medicationName",
        "drug",
        "drug_name",
        "drugName",
        "prescription",
        "rx",
        "medicine",
        "pharmaceutical",
        "treatment_drug",
        "treatmentDrug",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    detectionPattern = null, // requires RxNorm / drug-name dictionary
    maskingReplacement = "[MEDICATION REDACTED]",
  ),

  /**
   * **Health Condition** — a diagnosis, illness, disability, or medical history entry. GDPR Article
   * 9 special category; HIPAA §164.502 core PHI type. Detection requires NLP.
   */
  HEALTH_CONDITION(
    displayName = "Health Condition",
    shortName = "HC",
    aliases =
      listOf(
        "condition",
        "health_condition",
        "healthCondition",
        "diagnosis",
        "medical_condition",
        "medicalCondition",
        "illness",
        "disease",
        "ailment",
        "symptoms",
        "medical_history",
        "medicalHistory",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA, SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_1,
    detectionPattern = null,
  ),

  /**
   * **Date of Service** — the date on which a medical procedure, visit, or treatment occurred.
   * HIPAA §164.514(b) identifies dates (other than year) as PHI.
   *
   * The detection pattern is **label-bound**: it only matches when one of the known field-name
   * tokens (e.g. `service_date`, `dateOfService`, `dos`) immediately precedes the date value. This
   * avoids false-positive overlap with [DATE_OF_BIRTH], which uses a generic free-form date
   * pattern. A bare date string like `2026-03-11` will therefore only match [DATE_OF_BIRTH].
   */
  DATE_OF_SERVICE(
    displayName = "Date of Service",
    shortName = "DOS",
    aliases =
      listOf(
        "date_of_service",
        "dateOfService",
        "service_date",
        "serviceDate",
        "treatment_date",
        "treatmentDate",
        "appointment_date",
        "appointmentDate",
        "visit_date",
        "visitDate",
        "encounter_date",
        "encounterDate",
      ),
    category = SensitiveCategory.HEALTH,
    domains = setOf(SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_2,
    // Label-bound: requires a recognised service-date field name before the date value.
    // Covers snake_case and camelCase aliases; ISO 8601 and locale date formats accepted.
    detectionPattern =
      Regex(
        """(?i)(?:date_of_service|dateOfService|service_date|serviceDate|treatment_date|treatmentDate|appointment_date|appointmentDate|visit_date|visitDate|encounter_date|encounterDate|dos)\s*[:=]\s*(?:\d{4}[\/\-.]\d{1,2}[\/\-.]\d{1,2}|\d{1,2}[\/\-.]\d{1,2}[\/\-.]\d{4})"""
      ),
    maskingReplacement = "[DATE REDACTED]",
  ),

  // ---------------------------------------------------------------------------
  // DEMOGRAPHIC
  // ---------------------------------------------------------------------------

  /**
   * **Date of Birth** — a core PII / PHI identifier. Both ISO 8601 and locale date formats are
   * detected. The entire value is redacted because even a partial DOB significantly narrows
   * re-identification search space.
   */
  DATE_OF_BIRTH(
    displayName = "Date of Birth",
    shortName = "DOB",
    aliases =
      listOf(
        "dob",
        "date_of_birth",
        "dateOfBirth",
        "birth_date",
        "birthDate",
        "birthday",
        "birth_day",
        "born_on",
        "bornOn",
        "date_of_birth_year",
      ),
    category = SensitiveCategory.DEMOGRAPHIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR, SensitivityDomain.HIPAA),
    tier = SensitivityTier.TIER_2,
    detectionPattern =
      Regex("""\b\d{4}[\/\-.]\d{1,2}[\/\-.]\d{1,2}\b|\b\d{1,2}[\/\-.]\d{1,2}[\/\-.]\d{4}\b"""),
    maskingReplacement = "[DOB REDACTED]",
  ),

  /**
   * **Age** — an individual's age in years. Detected when associated with a labelled field. Tier 4:
   * low-risk alone, but contributes to re-identification alongside other attributes.
   */
  AGE(
    displayName = "Age",
    shortName = "AGE",
    aliases =
      listOf(
        "age",
        "years_old",
        "yearsOld",
        "age_years",
        "ageYears",
        "current_age",
        "currentAge",
        "age_at_time",
        "ageAtTime",
      ),
    category = SensitiveCategory.DEMOGRAPHIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_4,
    detectionPattern = Regex("""(?i)\bage\s*[:=]?\s*(\d{1,3})\b"""),
  ),

  /**
   * **Gender / Sex** — detected when a labelled field contains a gender value. Tier 4: limited risk
   * alone; however, special-category data under GDPR in certain jurisdictions when combined with
   * health context.
   */
  GENDER(
    displayName = "Gender",
    shortName = "GEN",
    aliases =
      listOf(
        "gender",
        "sex",
        "gender_identity",
        "genderIdentity",
        "biological_sex",
        "biologicalSex",
        "gender_code",
        "genderCode",
      ),
    category = SensitiveCategory.DEMOGRAPHIC,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_4,
    detectionPattern = Regex("""(?i)\b(?:gender|sex)\s*[:=]\s*([a-z]+)\b"""),
  ),

  /**
   * **Ethnicity / Race** — GDPR Article 9 special category; requires explicit consent to process.
   * Cannot be reliably detected with regex; alias-based matching is authoritative.
   */
  ETHNICITY(
    displayName = "Ethnicity / Race",
    shortName = "ETH",
    aliases =
      listOf(
        "ethnicity",
        "race",
        "ethnic_group",
        "ethnicGroup",
        "ethnic_background",
        "ethnicBackground",
        "racial_group",
        "racialGroup",
        "ethnic_origin",
        "ethnicOrigin",
      ),
    category = SensitiveCategory.DEMOGRAPHIC,
    domains = setOf(SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_3,
    detectionPattern = null,
  ),

  /**
   * **Nationality / Citizenship** — country of birth or legal citizenship status. Minimal-risk
   * alone; can contribute to discrimination or profiling when combined.
   */
  NATIONALITY(
    displayName = "Nationality",
    shortName = "NAT",
    aliases =
      listOf(
        "nationality",
        "citizenship",
        "country_of_origin",
        "countryOfOrigin",
        "country_of_birth",
        "countryOfBirth",
        "national_origin",
        "nationalOrigin",
      ),
    category = SensitiveCategory.DEMOGRAPHIC,
    domains = setOf(SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_4,
    detectionPattern = null,
  ),

  // ---------------------------------------------------------------------------
  // NETWORK IDENTIFIERS
  // ---------------------------------------------------------------------------

  /**
   * **IP Address (IPv4)** — personal data under GDPR (CJEU, Breyer v Germany, 2016). The first
   * octet (indicative of country/ISP) is preserved; the host portions are masked.
   */
  IP_ADDRESS(
    displayName = "IP Address",
    shortName = "IP",
    aliases =
      listOf(
        "ip",
        "ip_address",
        "ipAddress",
        "ip_addr",
        "ipAddr",
        "client_ip",
        "clientIp",
        "remote_addr",
        "remoteAddr",
        "source_ip",
        "sourceIp",
        "x_forwarded_for",
        "xForwardedFor",
        "x-forwarded-for",
        "remote_ip",
        "remoteIp",
      ),
    category = SensitiveCategory.NETWORK_IDENTIFIER,
    domains = setOf(SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_3,
    // Groups: (1) first octet (revealed), (2-4) host octets (masked)
    detectionPattern = Regex("""\b(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\b"""),
    maskingReplacement = "$1.*.*.*",
  ),

  /**
   * **MAC Address** — hardware-level network interface identifier. The OUI (first three octets
   * identifying the manufacturer) is preserved; the NIC-specific portion is masked to balance
   * debuggability and privacy.
   */
  MAC_ADDRESS(
    displayName = "MAC Address",
    shortName = "MAC",
    aliases =
      listOf(
        "mac",
        "mac_address",
        "macAddress",
        "hardware_address",
        "hardwareAddress",
        "physical_address",
        "physicalAddress",
        "ethernet_address",
        "ethernetAddress",
        "wifi_mac",
        "wifiMac",
      ),
    category = SensitiveCategory.NETWORK_IDENTIFIER,
    domains = setOf(SensitivityDomain.PII),
    tier = SensitivityTier.TIER_3,
    // Groups: (1-3) OUI octets (revealed), (4-6) NIC octets (masked)
    detectionPattern =
      Regex(
        """\b([0-9A-Fa-f]{2})[:\-]([0-9A-Fa-f]{2})[:\-]([0-9A-Fa-f]{2})[:\-][0-9A-Fa-f]{2}[:\-][0-9A-Fa-f]{2}[:\-][0-9A-Fa-f]{2}\b"""
      ),
    maskingReplacement = "$1:$2:$3:*:*:*",
  ),

  /**
   * **Device ID / UDID / IMEI** — a persistent hardware or software identifier that can track an
   * individual across sessions. Detected when a device-ID label precedes the value.
   */
  DEVICE_ID(
    displayName = "Device ID",
    shortName = "DID",
    aliases =
      listOf(
        "device_id",
        "deviceId",
        "udid",
        "imei",
        "meid",
        "device_identifier",
        "deviceIdentifier",
        "device_fingerprint",
        "deviceFingerprint",
        "hardware_id",
        "hardwareId",
        "android_id",
        "androidId",
        "idfa",
        "idfv",
      ),
    category = SensitiveCategory.NETWORK_IDENTIFIER,
    domains = setOf(SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_3,
    detectionPattern =
      Regex(
        """(?i)(?:device[_-]?id|udid|imei|meid|device[_-]?identifier|hardware[_-]?id|android[_-]?id|idfa|idfv)\s*[:=]\s*([A-Za-z0-9\-]{8,})"""
      ),
  ),

  /**
   * **Session ID** — a server-assigned identifier that links requests to an authenticated user
   * session. Exposure enables session hijacking.
   */
  SESSION_ID(
    displayName = "Session ID",
    shortName = "SID",
    aliases =
      listOf(
        "session_id",
        "sessionId",
        "sid",
        "jsessionid",
        "PHPSESSID",
        "asp_net_sessionid",
        "aspNetSessionId",
      ),
    category = SensitiveCategory.NETWORK_IDENTIFIER,
    domains = setOf(SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_3,
    detectionPattern =
      Regex(
        """(?i)(?:session[_-]?id|sessionid|jsessionid|phpsessid|asp[_-]?net[_-]?sessionid)\s*[:=]\s*([A-Za-z0-9\-]{8,})"""
      ),
  ),

  /**
   * **Cookie / Tracking ID** — persistent browser-based identifiers used for tracking. Detects
   * common analytics and advertising cookie names.
   */
  COOKIE_ID(
    displayName = "Cookie / Tracking ID",
    shortName = "CID",
    aliases =
      listOf(
        "cookie",
        "cookie_id",
        "cookieId",
        "tracking_cookie",
        "trackingCookie",
        "_ga",
        "_gid",
        "_fbp",
        "_fbc",
        "user_cookie",
        "userCookie",
        "visitor_id",
        "visitorId",
      ),
    category = SensitiveCategory.NETWORK_IDENTIFIER,
    domains = setOf(SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_3,
    detectionPattern =
      Regex(
        """(?i)(?:_ga|_gid|_fbp|_fbc|cookie[_-]?id|visitor[_-]?id)\s*[:=]\s*([A-Za-z0-9\-_.]{8,})"""
      ),
  ),

  // ---------------------------------------------------------------------------
  // LOCATION
  // ---------------------------------------------------------------------------

  /**
   * **GPS Coordinates** — a latitude/longitude pair identifying a precise physical location. Full
   * redaction is applied; no partial reveal is meaningful.
   */
  GPS_COORDINATES(
    displayName = "GPS Coordinates",
    shortName = "GPS",
    aliases =
      listOf(
        "gps",
        "gps_coordinates",
        "gpsCoordinates",
        "latitude_longitude",
        "latLong",
        "lat_lng",
        "latLng",
        "coordinates",
        "geo_coordinates",
        "geoCoordinates",
        "lat",
        "lon",
        "longitude",
        "latitude",
      ),
    category = SensitiveCategory.LOCATION,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_2,
    // Decimal degree format: [-+]lat, [-+]lon
    detectionPattern =
      Regex(
        """[-+]?(?:[1-8]?\d(?:\.\d+)?|90(?:\.0+)?),\s*[-+]?(?:180(?:\.0+)?|(?:1[0-7]\d|[1-9]?\d)(?:\.\d+)?)"""
      ),
    maskingReplacement = "[LOCATION REDACTED]",
  ),

  /**
   * **Postal Code** — a short alphanumeric code designating a mail delivery zone. Tier 4: limited
   * risk alone; however, combined with other attributes it can narrow population down to a few
   * hundred individuals. US ZIP+4 format detected.
   */
  POSTAL_CODE(
    displayName = "Postal Code",
    shortName = "ZIP",
    aliases =
      listOf(
        "zip",
        "zip_code",
        "zipCode",
        "postal_code",
        "postalCode",
        "postcode",
        "post_code",
        "pin_code",
        "pinCode",
        "area_code",
        "areaCode",
      ),
    category = SensitiveCategory.LOCATION,
    domains = setOf(SensitivityDomain.PII, SensitivityDomain.GDPR),
    tier = SensitivityTier.TIER_4,
    // US ZIP and ZIP+4; note: 5-digit matches may produce false positives without context
    detectionPattern = Regex("""\b\d{5}(?:-\d{4})?\b"""),
    maskingReplacement = "[ZIP REDACTED]",
  ),

  /**
   * **Precise Location** — real-time or historical location data with sub-100 m accuracy, typically
   * derived from GPS, Wi-Fi triangulation, or cell towers. Cannot be detected by regex; alias-based
   * matching and integration-layer tagging are primary controls.
   */
  PRECISE_LOCATION(
    displayName = "Precise Location",
    shortName = "LOC",
    aliases =
      listOf(
        "location",
        "precise_location",
        "preciseLocation",
        "real_time_location",
        "realtimeLocation",
        "current_location",
        "currentLocation",
        "live_location",
        "liveLocation",
        "geo_location",
        "geolocation",
        "user_location",
        "userLocation",
      ),
    category = SensitiveCategory.LOCATION,
    domains = setOf(SensitivityDomain.GDPR, SensitivityDomain.PII),
    tier = SensitivityTier.TIER_2,
    detectionPattern = null,
    maskingReplacement = "[LOCATION REDACTED]",
  );

  // ---------------------------------------------------------------------------
  // Instance API
  // ---------------------------------------------------------------------------

  /**
   * Returns all [MatchResult]s found by [detectionPattern] within [text]. Returns an empty list if
   * [detectionPattern] is `null`.
   */
  fun detect(text: String): List<MatchResult> =
    detectionPattern?.findAll(text)?.toList() ?: emptyList()

  /**
   * Replaces every occurrence detected by [detectionPattern] in [text] with [maskingReplacement].
   * Returns [text] unchanged when [detectionPattern] is `null` (i.e. alias-based elements cannot
   * perform in-text masking).
   */
  fun maskInText(text: String): String = detectionPattern?.replace(text, maskingReplacement) ?: text

  /**
   * Masks [value] as a standalone known-sensitive string.
   * - When [detectionPattern] is `null` (alias-only element): returns [maskingReplacement].
   * - When [detectionPattern] matches in [value]: replaces all matches with [maskingReplacement].
   * - When [detectionPattern] is non-null but finds no match (e.g. an unexpected format): still
   *   returns [maskingReplacement] rather than leaking [value] as plaintext.
   */
  fun maskValue(value: String): String =
    when {
      detectionPattern == null -> maskingReplacement
      detectionPattern.containsMatchIn(value) -> detectionPattern.replace(value, maskingReplacement)
      else -> maskingReplacement
    }

  /** Whether pattern-based in-text scanning is available for this element. */
  val isPatternDetectable: Boolean
    get() = detectionPattern != null

  // ---------------------------------------------------------------------------
  // Companion API
  // ---------------------------------------------------------------------------

  companion object {

    /**
     * Scans [text] for every [SensitiveElement] that has a [detectionPattern], returning only those
     * with at least one match. Useful for automated PII/PHI scanning of log lines or
     * request/response payloads.
     */
    fun scanText(text: String): Map<SensitiveElement, List<MatchResult>> =
      entries
        .filter { it.isPatternDetectable }
        .associateWith { it.detect(text) }
        .filterValues { it.isNotEmpty() }

    /**
     * Returns the [SensitiveElement] whose [aliases] list contains [alias] (case-insensitive), or
     * `null` if no element matches. Useful for resolving field names observed during capture to
     * their canonical [SensitiveElement].
     *
     * @throws IllegalStateException if more than one element claims the same alias, which indicates
     *   a data-definition bug that must be fixed at compile time.
     */
    fun forAlias(alias: String): SensitiveElement? {
      val normalised = alias.lowercase()
      val matches =
        entries.filter { element -> element.aliases.any { it.lowercase() == normalised } }
      return when (matches.size) {
        0 -> null
        1 -> matches.first()
        else ->
          throw IllegalStateException(
            "Alias '$alias' is claimed by multiple SensitiveElement entries: " +
              matches.joinToString { it.name }
          )
      }
    }

    /** Returns all [SensitiveElement] entries belonging to the given [category]. */
    fun byCategory(category: SensitiveCategory): List<SensitiveElement> =
      entries.filter { it.category == category }

    /** Returns all [SensitiveElement] entries that are governed by the given [domain]. */
    fun byDomain(domain: SensitivityDomain): List<SensitiveElement> =
      entries.filter { domain in it.domains }

    /**
     * Returns all [SensitiveElement] entries at or above (i.e. equal to or more sensitive than) the
     * given [tier]. For example, passing [SensitivityTier.TIER_1] returns all Tier 0 and Tier 1
     * elements.
     */
    fun atOrAboveTier(tier: SensitivityTier): List<SensitiveElement> =
      entries.filter { it.tier.level <= tier.level }
  }
}
