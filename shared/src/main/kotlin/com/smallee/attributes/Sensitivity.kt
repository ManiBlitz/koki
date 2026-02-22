package com.smallee.attributes

enum class Sensitivity {
  /** Defines attributes that have a high privacy level and should be fully redacted */
  HIGHLY_SENSITIVE {
    override fun <T> obfuscate(value: T): String = "[REDACTED]"
  },

  /** Defines attributes that are sensitive and should be masked */
  SENSITIVE {
    private fun maskString(s: String): String =
      when {
        s.length <= 4 -> "****"
        s.length == 5 -> "${s[0]}****"
        else -> "${s.substring(0, 2)}****"
      }

    override fun <T> obfuscate(value: T): String =
      when (value) {
        null -> "****"
        is Boolean -> "****"
        is List<*> -> value.joinToString(separator = ", ") { obfuscate(it) }
        else -> maskString(value.toString())
      }
  },

  /** Define attributes that are safe and can be printed as-is */
  SAFE {
    override fun <T> obfuscate(value: T): String = value.toString()
  };

  /** Provides an obfuscation value based on the provided string. */
  abstract fun <T> obfuscate(value: T): String
}
