package com.smallee.metrics

import com.smallee.attributes.AttributeEntry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes

internal object MetricAttributes {
  fun build(entries: List<AttributeEntry<*>>): Attributes {
    val builder = Attributes.builder()
    entries.forEach { entry ->
      if (entry.value != null) {
        @Suppress("UNCHECKED_CAST")
        builder.put(entry.definition as AttributeKey<Any>, entry.value as Any)
      }
    }
    return builder.build()
  }
}
