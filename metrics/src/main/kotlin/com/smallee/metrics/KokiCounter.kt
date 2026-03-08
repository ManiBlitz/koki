package com.smallee.metrics

import com.smallee.attributes.AttributeEntry

/** Central interface for our counter metric definition */
interface KokiCounter {

  /** Returns the name of the counter instrument */
  fun getName(): String

  /**
   * Adds the given value to the counter
   *
   * @param value the amount to add; must be non-negative per the OpenTelemetry specification
   * @param attributes the attribute entries that will be attached to this measurement
   * @see AttributeEntry
   */
  fun add(value: Long, vararg attributes: AttributeEntry<*> = emptyArray())

  /**
   * Increments the counter by 1 with the given attributes. Shorthand for [add] with value `1`.
   *
   * @param attributes the attribute entries that will be attached to this measurement
   * @see AttributeEntry
   */
  fun increment(vararg attributes: AttributeEntry<*> = emptyArray()) = add(1L, *attributes)
}
