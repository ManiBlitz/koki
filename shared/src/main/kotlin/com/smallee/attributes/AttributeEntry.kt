package com.smallee.attributes

/**
 * Defines an entry of AttributeDefinition and its corresponding value They should match in terms of
 * the type being used.
 *
 * @property definition the [AttributeDefinition] object defining the attribute
 * @property value the value of the Attribute. This value is nullable.
 * @see AttributeDefinition
 * @see Sensitivity
 */
data class AttributeEntry<T>(val definition: AttributeDefinition<T>, val value: T?)
