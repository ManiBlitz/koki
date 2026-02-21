package attributes

enum class Sensitivity {
  /** Defines attributes that have a high privacy level and should be fully redacted */
  HIGHLY_SENSITIVE {
    override fun obfuscate(value: String): String = "[REDACTED]"
  },

  /** Defines attributes that are sensitive and should be masked */
  SENSITIVE {
    override fun obfuscate(value: String): String =
      StringBuilder().append(value.substring(0, 2)).append("****").toString()
  },

  /** Define attributes that are safe and can be printed as-is */
  SAFE {
    override fun obfuscate(value: String): String = value
  };

  /** Provides an obfuscation value based on the provided string. */
  abstract fun obfuscate(value: String): String
}
