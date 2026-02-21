package attributes

import io.opentelemetry.api.common.AttributeType
import java.util.WeakHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AttributeDefinitionTest {

  /**
   * Uses reflection to access the private instances map on AttributeDefinition. The Kotlin compiler
   * promotes companion object private properties into static fields on the enclosing class, so the
   * field lives directly on AttributeDefinition.
   */
  @Suppress("UNCHECKED_CAST")
  private fun getInstances(): WeakHashMap<String, AttributeDefinition<*>> {
    val instancesField = AttributeDefinition::class.java.getDeclaredField("instances")
    instancesField.isAccessible = true
    return instancesField.get(null) as WeakHashMap<String, AttributeDefinition<*>>
  }

  @Test
  fun `createString stores an instance with STRING type`() {
    AttributeDefinition.createString("attr.string", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.string"]
    assertNotNull(instance)
    assertEquals("attr.string", instance.getKey())
    assertEquals(AttributeType.STRING, instance.getType())
  }

  @Test
  fun `createBoolean stores an instance with BOOLEAN type`() {
    AttributeDefinition.createBoolean("attr.boolean", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.boolean"]
    assertNotNull(instance)
    assertEquals("attr.boolean", instance.getKey())
    assertEquals(AttributeType.BOOLEAN, instance.getType())
  }

  @Test
  fun `createLong stores an instance with LONG type`() {
    AttributeDefinition.createLong("attr.long", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.long"]
    assertNotNull(instance)
    assertEquals("attr.long", instance.getKey())
    assertEquals(AttributeType.LONG, instance.getType())
  }

  @Test
  fun `createDouble stores an instance with DOUBLE type`() {
    AttributeDefinition.createDouble("attr.double", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.double"]
    assertNotNull(instance)
    assertEquals("attr.double", instance.getKey())
    assertEquals(AttributeType.DOUBLE, instance.getType())
  }

  @Test
  fun `createStringArray stores an instance with STRING_ARRAY type`() {
    AttributeDefinition.createStringArray("attr.string.array", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.string.array"]
    assertNotNull(instance)
    assertEquals("attr.string.array", instance.getKey())
    assertEquals(AttributeType.STRING_ARRAY, instance.getType())
  }

  @Test
  fun `createBooleanArray stores an instance with BOOLEAN_ARRAY type`() {
    AttributeDefinition.createBooleanArray("attr.boolean.array", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.boolean.array"]
    assertNotNull(instance)
    assertEquals("attr.boolean.array", instance.getKey())
    assertEquals(AttributeType.BOOLEAN_ARRAY, instance.getType())
  }

  @Test
  fun `createLongArray stores an instance with LONG_ARRAY type`() {
    AttributeDefinition.createLongArray("attr.long.array", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.long.array"]
    assertNotNull(instance)
    assertEquals("attr.long.array", instance.getKey())
    assertEquals(AttributeType.LONG_ARRAY, instance.getType())
  }

  @Test
  fun `createDoubleArray stores an instance with DOUBLE_ARRAY type`() {
    AttributeDefinition.createDoubleArray("attr.double.array", Sensitivity.SAFE, false)
    val instance = getInstances()["attr.double.array"]
    assertNotNull(instance)
    assertEquals("attr.double.array", instance.getKey())
    assertEquals(AttributeType.DOUBLE_ARRAY, instance.getType())
  }

  @Test
  fun `createString is idempotent - second call with same name does not overwrite`() {
    val name = "attr.idempotent.string"
    AttributeDefinition.createString(name, Sensitivity.SAFE, false)
    val firstInstance = getInstances()[name]

    AttributeDefinition.createString(name, Sensitivity.HIGHLY_SENSITIVE, true)
    val secondInstance = getInstances()[name]

    assertEquals(firstInstance, secondInstance)
  }

  @Test
  fun `sensitivity is stored correctly on string attribute`() {
    AttributeDefinition.createString("attr.sensitive.string", Sensitivity.HIGHLY_SENSITIVE, false)
    val instance = getInstances()["attr.sensitive.string"]
    assertNotNull(instance)
  }

  @Test
  fun `isRequiredForContext is accepted without error`() {
    AttributeDefinition.createString("attr.context.required", Sensitivity.SAFE, true)
    val instance = getInstances()["attr.context.required"]
    assertNotNull(instance)
  }
}
