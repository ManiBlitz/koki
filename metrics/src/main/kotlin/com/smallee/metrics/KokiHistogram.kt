package com.smallee.metrics

import com.smallee.attributes.AttributeEntry

/** Central interface for our histogram metric definition */
interface KokiHistogram {

  /** Returns the name of the histogram instrument */
  fun getName(): String

  /**
   * Records the given value as an observation in the histogram
   *
   * @param value the measurement to record; must be non-negative per the OpenTelemetry
   *   specification
   * @param attributes the attribute entries that will be attached to this measurement
   * @see AttributeEntry
   */
  fun record(value: Double, vararg attributes: AttributeEntry<*> = emptyArray())
}
