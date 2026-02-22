package com.smallee.logger

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LogFactoryTest {

  @Test
  fun `create by name returns KokiLogger with the given name`() {
    val logger = LogFactory.create("my-logger")
    assertEquals("my-logger", logger.getName())
  }

  @Test
  fun `create by name returns a Logger instance`() {
    val logger = LogFactory.create("logger-type-check")
    assertIs<Logger>(logger)
  }

  @Test
  fun `create by name with empty string returns Logger with empty name`() {
    val logger = LogFactory.create("")
    assertEquals("", logger.getName())
  }

  @Test
  fun `create by Class returns KokiLogger named after the class`() {
    val logger = LogFactory.create(String::class.java)
    assertNotNull(logger)
    assertEquals("java.lang.String", logger.getName())
  }

  @Test
  fun `create by Class returns a Logger instance`() {
    val logger = LogFactory.create(Int::class.java)
    assertIs<Logger>(logger)
  }

  @Test
  fun `create by Class uses the fully qualified class name`() {
    val logger = LogFactory.create(LogFactory::class.java)
    assertEquals("com.smallee.logger.LogFactory", logger.getName())
  }

  @Test
  fun `create by reified type returns KokiLogger named after the type`() {
    val logger = LogFactory.create<String>()
    assertNotNull(logger)
    assertEquals("java.lang.String", logger.getName())
  }

  @Test
  fun `create by reified type returns a Logger instance`() {
    val logger = LogFactory.create<Int>()
    assertIs<Logger>(logger)
  }

  @Test
  fun `create by reified type and create by Class produce the same name`() {
    val byClass = LogFactory.create(String::class.java)
    val byReified = LogFactory.create<String>()
    assertEquals(byClass.getName(), byReified.getName())
  }

  @Test
  fun `each call to create returns a distinct Logger instance`() {
    val first = LogFactory.create("logger-a")
    val second = LogFactory.create("logger-b")
    assertEquals("logger-a", first.getName())
    assertEquals("logger-b", second.getName())
  }
}
