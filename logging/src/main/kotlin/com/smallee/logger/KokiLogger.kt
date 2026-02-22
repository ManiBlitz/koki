package com.smallee.logger

import com.smallee.attributes.AttributeEntry

/** Central interface for our logger definition */
interface KokiLogger {

  /** Returns the name of the logger instance */
  fun getName(): String

  /**
   * Enables the capture of debug logs for our logger
   *
   * @param body the log message body
   * @param attributes the attribute entries that will be added to the log message
   */
  fun debug(body: String, vararg attributes: AttributeEntry<*> = emptyArray())

  /**
   * Enables the capture of info logs for our logger
   *
   * @param body the log message body
   * @param attributes the attribute entries that will be added to the log message
   */
  fun info(body: String, vararg attributes: AttributeEntry<*> = emptyArray())

  /**
   * Enables the capture of warn logs for our logger
   *
   * @param body the log message body
   * @param attributes the attribute entries that will be added to the log message
   * @param cause the exception captured for the log entry
   */
  fun warn(
    body: String,
    vararg attributes: AttributeEntry<*> = emptyArray(),
    cause: Throwable? = null,
  )

  /**
   * Enables the capture of error logs for our logger
   *
   * @param body the log message body
   * @param attributes the attribute entries that will be added to the log message
   * @param cause the exception captured for the log entry
   */
  fun error(
    body: String,
    vararg attributes: AttributeEntry<*> = emptyArray(),
    cause: Throwable? = null,
  )
}
