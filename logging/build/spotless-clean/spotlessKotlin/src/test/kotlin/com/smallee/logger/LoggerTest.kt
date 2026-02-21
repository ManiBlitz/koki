package com.smallee.logger

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoggerTest {

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
  fun `getName returns name with special characters unchanged`() {
    val name = "com.smallee.service.UserService"
    val logger = Logger(name)
    assertEquals(name, logger.getName())
  }

  @Test
  fun `isTraceEnabled throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.isTraceEnabled() }
  }

  @Test
  fun `isDebugEnabled throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.isDebugEnabled() }
  }

  @Test
  fun `isInfoEnabled throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.isInfoEnabled() }
  }

  @Test
  fun `isWarnEnabled throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.isWarnEnabled() }
  }

  @Test
  fun `isErrorEnabled throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.isErrorEnabled() }
  }

  @Test
  fun `trace throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.trace("message") }
  }

  @Test
  fun `debug throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.debug("message") }
  }

  @Test
  fun `info throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.info("message") }
  }

  @Test
  fun `warn throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.warn("message") }
  }

  @Test
  fun `error throws NotImplementedError`() {
    val logger = Logger("test")
    assertFailsWith<NotImplementedError> { logger.error("message") }
  }
}
