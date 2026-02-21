package com.smallee

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

class MainTest {

  @Test
  fun `main prints Hello World`() {
    val output = ByteArrayOutputStream()
    val original = System.out
    System.setOut(PrintStream(output))
    try {
      main()
    } finally {
      System.setOut(original)
    }
    assertEquals("Hello World!", output.toString().trim())
  }
}
