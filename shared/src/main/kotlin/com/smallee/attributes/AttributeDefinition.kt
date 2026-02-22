package com.smallee.attributes

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeType
import java.util.concurrent.ConcurrentHashMap

/**
 * Defines an attributeKey with additional parameters for sensitivity considerations and propagation
 * considerations It applies the same parameterization as the [AttributeKey], allowing for a compile
 * time validation of the attribute value.
 *
 * @property attributeName the name of the attribute
 * @property attributeType the [AttributeType] provided for the attribute
 * @property sensitivity the sensitivity level of the attribute
 * @property isRequiredForContext defines if the attribute is required for context propagation
 * @see AttributeType
 * @see AttributeKey
 */
class AttributeDefinition<T>
private constructor(
  private val attributeName: String,
  private val attributeType: AttributeType = AttributeType.STRING,
  private val sensitivity: Sensitivity = Sensitivity.SAFE,
  private val isRequiredForContext: Boolean = false,
) : AttributeKey<T> {
  companion object {

    /**
     * Contains all the current instances of the AttributeDefinition when created. Definitions are
     * permanent schema — once registered, an entry is never evicted.
     */
    private val instances: ConcurrentHashMap<String, AttributeDefinition<*>> = ConcurrentHashMap()

    /**
     * Registers [factory] under [name] if absent and returns the stored instance. The cast is safe
     * because each public factory method controls exactly which [AttributeDefinition] type it
     * inserts for a given name.
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> register(
      name: String,
      factory: () -> AttributeDefinition<T>,
    ): AttributeDefinition<T> =
      instances.computeIfAbsent(name) { factory() } as AttributeDefinition<T>

    /**
     * Enables the definition of an attribute that maps to a string value
     *
     * @see Sensitivity
     */
    fun createString(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<String> =
      register(name) {
        AttributeDefinition(name, AttributeType.STRING, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of boolean attributes
     *
     * @see Sensitivity
     */
    fun createBoolean(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<Boolean> =
      register(name) {
        AttributeDefinition(name, AttributeType.BOOLEAN, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of long attributes
     *
     * @see Sensitivity
     */
    fun createLong(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<Long> =
      register(name) {
        AttributeDefinition(name, AttributeType.LONG, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of a double attribute
     *
     * @see Sensitivity
     */
    fun createDouble(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<Double> =
      register(name) {
        AttributeDefinition(name, AttributeType.DOUBLE, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of a string array attribute
     *
     * @see Sensitivity
     */
    fun createStringArray(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<List<String>> =
      register(name) {
        AttributeDefinition(name, AttributeType.STRING_ARRAY, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of a Boolean array attribute
     *
     * @see Sensitivity
     */
    fun createBooleanArray(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<List<Boolean>> =
      register(name) {
        AttributeDefinition(name, AttributeType.BOOLEAN_ARRAY, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of a long array attribute
     *
     * @see Sensitivity
     */
    fun createLongArray(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<List<Long>> =
      register(name) {
        AttributeDefinition(name, AttributeType.LONG_ARRAY, sensitivity, isRequiredForContext)
      }

    /**
     * Enables the definition of a double array attribute
     *
     * @see Sensitivity
     */
    fun createDoubleArray(
      name: String,
      sensitivity: Sensitivity,
      isRequiredForContext: Boolean,
    ): AttributeDefinition<List<Double>> =
      register(name) {
        AttributeDefinition(name, AttributeType.DOUBLE_ARRAY, sensitivity, isRequiredForContext)
      }
  }

  override fun getKey(): String = attributeName

  override fun getType(): AttributeType = attributeType

  fun getSensitivity(): Sensitivity = sensitivity
}
