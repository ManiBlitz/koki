package com.smallee.logger

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry
import com.smallee.attributes.Sensitivity
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggerTest {

  private fun stringEntry(
    name: String,
    value: String,
    sensitivity: Sensitivity,
  ): AttributeEntry<String> =
    AttributeEntry(AttributeDefinition.createString(name, sensitivity, false), value)

  // ── getName ────────────────────────────────────────────────────────────────

  @Test
  fun `getName returns the name passed to the constructor`() {
    val logger = Logger("my-service")
    assertEquals("my-service", logger.getName())
  }

  @Test
  fun `getName returns empty string when constructed with empty name`() {
    val logger = Logger("")
    assertEquals("", logger.getName())
  }

  @Test
  fun `getName returns fully qualified class name unchanged`() {
    val name = "com.smallee.service.UserService"
    val logger = Logger(name)
    assertEquals(name, logger.getName())
  }

  // ── debug ──────────────────────────────────────────────────────────────────

  @Test
  fun `debug logs without attributes without throwing`() {
    Logger("test.debug").debug("debug message")
  }

  @Test
  fun `debug logs with a SAFE attribute without throwing`() {
    val entry = stringEntry("log.debug.safe", "public-value", Sensitivity.SAFE)
    Logger("test.debug.safe").debug("debug message", entry)
  }

  @Test
  fun `debug logs with a SENSITIVE attribute without throwing`() {
    val entry = stringEntry("log.debug.sensitive", "partial-secret", Sensitivity.SENSITIVE)
    Logger("test.debug.sensitive").debug("debug message", entry)
  }

  @Test
  fun `debug logs with a HIGHLY_SENSITIVE attribute without throwing`() {
    val entry = stringEntry("log.debug.hs", "top-secret", Sensitivity.HIGHLY_SENSITIVE)
    Logger("test.debug.hs").debug("debug message", entry)
  }

  @Test
  fun `debug logs with multiple attributes without throwing`() {
    val safe = stringEntry("log.debug.multi.safe", "public", Sensitivity.SAFE)
    val sensitive = stringEntry("log.debug.multi.sensitive", "partial", Sensitivity.SENSITIVE)
    val redacted = stringEntry("log.debug.multi.hs", "secret", Sensitivity.HIGHLY_SENSITIVE)
    Logger("test.debug.multi").debug("debug message", safe, sensitive, redacted)
  }

  // ── info ───────────────────────────────────────────────────────────────────

  @Test
  fun `info logs without attributes without throwing`() {
    Logger("test.info").info("info message")
  }

  @Test
  fun `info logs with a SAFE attribute without throwing`() {
    val entry = stringEntry("log.info.safe", "public-value", Sensitivity.SAFE)
    Logger("test.info.safe").info("info message", entry)
  }

  @Test
  fun `info logs with a SENSITIVE attribute without throwing`() {
    val entry = stringEntry("log.info.sensitive", "partial-secret", Sensitivity.SENSITIVE)
    Logger("test.info.sensitive").info("info message", entry)
  }

  @Test
  fun `info logs with a HIGHLY_SENSITIVE attribute without throwing`() {
    val entry = stringEntry("log.info.hs", "top-secret", Sensitivity.HIGHLY_SENSITIVE)
    Logger("test.info.hs").info("info message", entry)
  }

  // ── warn ───────────────────────────────────────────────────────────────────

  @Test
  fun `warn logs without attributes and without cause without throwing`() {
    Logger("test.warn").warn("warn message")
  }

  @Test
  fun `warn logs with a cause without throwing`() {
    Logger("test.warn.cause").warn("warn message", cause = RuntimeException("something went wrong"))
  }

  @Test
  fun `warn logs with a SAFE attribute without throwing`() {
    val entry = stringEntry("log.warn.safe", "public-value", Sensitivity.SAFE)
    Logger("test.warn.safe").warn("warn message", entry)
  }

  @Test
  fun `warn logs with a SENSITIVE attribute and a cause without throwing`() {
    val entry = stringEntry("log.warn.sensitive", "partial-secret", Sensitivity.SENSITIVE)
    Logger("test.warn.sensitive")
      .warn("warn message", entry, cause = RuntimeException("something went wrong"))
  }

  // ── error ──────────────────────────────────────────────────────────────────

  @Test
  fun `error logs without attributes and without cause without throwing`() {
    Logger("test.error").error("error message")
  }

  @Test
  fun `error logs with a cause without throwing`() {
    Logger("test.error.cause").error("error message", cause = RuntimeException("fatal error"))
  }

  @Test
  fun `error logs with a SAFE attribute without throwing`() {
    val entry = stringEntry("log.error.safe", "public-value", Sensitivity.SAFE)
    Logger("test.error.safe").error("error message", entry)
  }

  @Test
  fun `error logs with a HIGHLY_SENSITIVE attribute and a cause without throwing`() {
    val entry = stringEntry("log.error.hs", "top-secret", Sensitivity.HIGHLY_SENSITIVE)
    Logger("test.error.hs").error("error message", entry, cause = RuntimeException("fatal error"))
  }
}
