package com.smallee.logger

import com.smallee.attributes.AttributeEntry
import org.slf4j.LoggerFactory
import org.slf4j.spi.LoggingEventBuilder

/**
 * Main logger. Implements [KokiLogger] to provide the base logging definition
 *
 * @param name the name of the logger
 * @see KokiLogger
 * @see AttributeEntry
 * @see AttributeDefinition
 * @see Sensitivity
 */
class Logger(private val name: String) : KokiLogger {

  /**
   * Defines an Slf4J delegate class that will handle the construction and delivery of logs
   *
   * @see LoggerFactory
   * @see LoggingEventBuilder
   */
  private val delegate = LoggerFactory.getLogger(name)

  override fun getName(): String = name

  override fun debug(body: String, vararg attributes: AttributeEntry<*>) {
    val builder = delegate.atDebug()
    buildAttributes(builder, attributes)
    builder.log(body)
  }

  override fun info(body: String, vararg attributes: AttributeEntry<*>) {
    val builder = delegate.atInfo()
    buildAttributes(builder, attributes)
    builder.log(body)
  }

  override fun warn(body: String, vararg attributes: AttributeEntry<*>, cause: Throwable?) {
    val builder = delegate.atWarn()
    buildAttributes(builder, attributes)
    builder.setCause(cause)
    builder.log(body)
  }

  override fun error(body: String, vararg attributes: AttributeEntry<*>, cause: Throwable?) {
    val builder = delegate.atError()
    buildAttributes(builder, attributes)
    builder.setCause(cause)
    builder.log(body)
  }

  /**
   * Enables the definition of attributes for our logs. It automatically obfuscates the attribute
   * values based on the sensitivity of the attribute
   *
   * @param builder the logging event builder provided
   * @param attributes the list of attribute entries that will be added to the log entry
   * @see LoggerFactory
   * @see LoggingEventBuilder
   * @see AttributeEntry
   */
  private fun buildAttributes(
    builder: LoggingEventBuilder,
    attributes: Array<out AttributeEntry<*>>,
  ) {
    attributes.forEach {
      val safeAttributeValue = it.definition.getSensitivity().obfuscate(it.value)
      builder.addKeyValue(it.definition.key, safeAttributeValue)
    }
  }
}
