package com.smallee.logger

object LogFactory {

  fun create(name: String): Logger {
    return Logger(name)
  }

  fun create(clazz: Class<*>): Logger {
    return Logger(clazz.name)
  }

  inline fun <reified T : Any> create(): Logger {
    return Logger(T::class.java.name)
  }
}
