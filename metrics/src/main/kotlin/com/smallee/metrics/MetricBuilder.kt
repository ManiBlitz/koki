package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry

/**
 * Generic builder for creating and configuring a metric instrument.
 *
 * Collects instrument configuration ([description], [unit]) and optional base tags that are merged
 * into every subsequent measurement. Call [build] once to obtain the fully configured instrument.
 *
 * Base tags added via [addTag], [addTagEntry], or [addAllTags] are always present alongside any
 * per-call attributes passed to [KokiCounter.add] / [KokiGauge.record] / [KokiHistogram.record].
 *
 * @param M the type of metric instrument produced by [build]
 * @param factory internal factory invoked by [build] to construct the underlying instrument
 * @see MetricFactory
 * @see KokiCounter
 * @see KokiGauge
 * @see KokiHistogram
 */
class MetricBuilder<M>
internal constructor(
  private val factory: (description: String, unit: String, baseTags: List<AttributeEntry<*>>) -> M
) {

  private var description: String = ""
  private var unit: String = ""
  private val baseTags: MutableList<AttributeEntry<*>> = mutableListOf()

  /**
   * Sets the human-readable description of the metric instrument
   *
   * @param description a sentence explaining what the metric measures
   */
  fun description(description: String): MetricBuilder<M> {
    this.description = description
    return this
  }

  /**
   * Sets the unit of measurement for the instrument, following the UCUM convention
   *
   * @param unit the unit string (e.g. "ms", "By", "{request}")
   */
  fun unit(unit: String): MetricBuilder<M> {
    this.unit = unit
    return this
  }

  /**
   * Adds a base tag from an [AttributeDefinition] and its corresponding typed value. The tag is
   * included in every measurement produced by the built instrument.
   *
   * @param definition the attribute definition that describes the tag
   * @param value the value for this attribute
   * @see AttributeDefinition
   * @see AttributeEntry
   */
  fun <T> addTag(definition: AttributeDefinition<T>, value: T): MetricBuilder<M> {
    baseTags.add(AttributeEntry(definition, value))
    return this
  }

  /**
   * Adds a base tag from a pre-built [AttributeEntry]. The tag is included in every measurement
   * produced by the built instrument.
   *
   * @param entry the attribute entry to attach
   * @see AttributeEntry
   */
  fun <T> addTagEntry(entry: AttributeEntry<T>): MetricBuilder<M> {
    baseTags.add(entry)
    return this
  }

  /**
   * Adds all entries in the given list as base tags. Each tag is included in every measurement
   * produced by the built instrument.
   *
   * @param entries the list of attribute entries to attach
   * @see AttributeEntry
   */
  fun addAllTags(entries: List<AttributeEntry<*>>): MetricBuilder<M> {
    baseTags.addAll(entries)
    return this
  }

  /**
   * Builds and returns the configured metric instrument. The underlying OpenTelemetry instrument is
   * created at this point using the accumulated configuration.
   *
   * @return the fully configured metric instrument
   */
  fun build(): M = factory(description, unit, baseTags.toList())
}

/** Type alias for a builder that produces a [KokiCounter] */
typealias CounterBuilder = MetricBuilder<KokiCounter>

/** Type alias for a builder that produces a [KokiGauge] */
typealias GaugeBuilder = MetricBuilder<KokiGauge>

/** Type alias for a builder that produces a [KokiHistogram] */
typealias HistogramBuilder = MetricBuilder<KokiHistogram>
