package com.smallee.logger

/** LogFactory allowing the definition of loggers for our service */
object LogFactory {

  /**
   * Allows for the creation of a new logger using the direct name definition
   *
   * @param name the name given to the logger
   * @see KokiLogger
   * @see Logger
   */
  fun create(name: String): KokiLogger {
    return Logger(name)
  }

  /**
   * Allows for the creation of a new logger using the class definition
   *
   * @param clazz the name given to the logger
   * @see KokiLogger
   * @see Logger
   */
  fun create(clazz: Class<*>): KokiLogger {
    return Logger(clazz.name)
  }

  /**
   * Allows for the creation of a new logger using the parameterized definition
   *
   * @see KokiLogger
   * @see Logger
   */
  inline fun <reified T : Any> create(): KokiLogger {
    return Logger(T::class.java.name)
  }
}
