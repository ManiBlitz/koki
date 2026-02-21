package attributes

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeType
import java.util.WeakHashMap

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
     * Contains all the current instances of the AttributeDefinition when created It is a map that
     * will contain a fixed number of attributes at a specific time
     */
    private val instances: WeakHashMap<String, AttributeDefinition<*>> =
      WeakHashMap.newWeakHashMap(500)

    /**
     * Enables the definition of an attribute that maps to a string value
     *
     * @see Sensitivity
     */
    fun createString(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<String>(name, AttributeType.STRING, sensitivity, isRequiredForContext)
        },
      )
    }

    /**
     * Enables the definition of boolean attributes
     *
     * @see Sensitivity
     */
    fun createBoolean(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<Boolean>(
            name,
            AttributeType.BOOLEAN,
            sensitivity,
            isRequiredForContext,
          )
        },
      )
    }

    /**
     * Enables the definition of long attributes
     *
     * @see Sensitivity
     */
    fun createLong(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        { AttributeDefinition<Long>(name, AttributeType.LONG, sensitivity, isRequiredForContext) },
      )
    }

    /**
     * Enables the definition of a double attributes
     *
     * @see Sensitivity
     */
    fun createDouble(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<Double>(name, AttributeType.DOUBLE, sensitivity, isRequiredForContext)
        },
      )
    }

    /**
     * Enables the definition of a string array attribute
     *
     * @see Sensitivity
     */
    fun createStringArray(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<List<String>>(
            name,
            AttributeType.STRING_ARRAY,
            sensitivity,
            isRequiredForContext,
          )
        },
      )
    }

    /**
     * Enables the definition of a Boolean array attribute
     *
     * @see Sensitivity
     */
    fun createBooleanArray(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<List<Boolean>>(
            name,
            AttributeType.BOOLEAN_ARRAY,
            sensitivity,
            isRequiredForContext,
          )
        },
      )
    }

    /**
     * Enables the definition of a long array attribute
     *
     * @see Sensitivity
     */
    fun createLongArray(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<List<Long>>(
            name,
            AttributeType.LONG_ARRAY,
            sensitivity,
            isRequiredForContext,
          )
        },
      )
    }

    /**
     * Enables the definition of a double array attribute
     *
     * @see Sensitivity
     */
    fun createDoubleArray(name: String, sensitivity: Sensitivity, isRequiredForContext: Boolean) {
      instances.computeIfAbsent(
        name,
        {
          AttributeDefinition<List<Double>>(
            name,
            AttributeType.DOUBLE_ARRAY,
            sensitivity,
            isRequiredForContext,
          )
        },
      )
    }
  }

  override fun getKey(): String = attributeName

  override fun getType(): AttributeType = attributeType
}
