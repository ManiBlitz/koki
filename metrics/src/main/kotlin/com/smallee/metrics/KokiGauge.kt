package com.smallee.metrics

import com.smallee.attributes.AttributeEntry

/** Central interface for our gauge metric definition */
interface KokiGauge {

  /** Returns the name of the gauge instrument */
  fun getName(): String

  /**
   * Records the current value of the gauge
   *
   * @param value the measurement to record; may be any finite double
   * @param attributes the attribute entries that will be attached to this measurement
   * @see AttributeEntry
   */
  fun record(value: Double, vararg attributes: AttributeEntry<*> = emptyArray())
}
