package attributes

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.Sensitivity
import io.opentelemetry.api.common.AttributeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AttributeDefinitionTest {

  @Test
  fun `createString returns an instance with STRING type`() {
    val instance = AttributeDefinition.createString("attr.string", Sensitivity.SAFE, false)
    assertEquals("attr.string", instance.getKey())
    assertEquals(AttributeType.STRING, instance.getType())
  }

  @Test
  fun `createBoolean returns an instance with BOOLEAN type`() {
    val instance = AttributeDefinition.createBoolean("attr.boolean", Sensitivity.SAFE, false)
    assertEquals("attr.boolean", instance.getKey())
    assertEquals(AttributeType.BOOLEAN, instance.getType())
  }

  @Test
  fun `createLong returns an instance with LONG type`() {
    val instance = AttributeDefinition.createLong("attr.long", Sensitivity.SAFE, false)
    assertEquals("attr.long", instance.getKey())
    assertEquals(AttributeType.LONG, instance.getType())
  }

  @Test
  fun `createDouble returns an instance with DOUBLE type`() {
    val instance = AttributeDefinition.createDouble("attr.double", Sensitivity.SAFE, false)
    assertEquals("attr.double", instance.getKey())
    assertEquals(AttributeType.DOUBLE, instance.getType())
  }

  @Test
  fun `createStringArray returns an instance with STRING_ARRAY type`() {
    val instance =
      AttributeDefinition.createStringArray("attr.string.array", Sensitivity.SAFE, false)
    assertEquals("attr.string.array", instance.getKey())
    assertEquals(AttributeType.STRING_ARRAY, instance.getType())
  }

  @Test
  fun `createBooleanArray returns an instance with BOOLEAN_ARRAY type`() {
    val instance =
      AttributeDefinition.createBooleanArray("attr.boolean.array", Sensitivity.SAFE, false)
    assertEquals("attr.boolean.array", instance.getKey())
    assertEquals(AttributeType.BOOLEAN_ARRAY, instance.getType())
  }

  @Test
  fun `createLongArray returns an instance with LONG_ARRAY type`() {
    val instance = AttributeDefinition.createLongArray("attr.long.array", Sensitivity.SAFE, false)
    assertEquals("attr.long.array", instance.getKey())
    assertEquals(AttributeType.LONG_ARRAY, instance.getType())
  }

  @Test
  fun `createDoubleArray returns an instance with DOUBLE_ARRAY type`() {
    val instance =
      AttributeDefinition.createDoubleArray("attr.double.array", Sensitivity.SAFE, false)
    assertEquals("attr.double.array", instance.getKey())
    assertEquals(AttributeType.DOUBLE_ARRAY, instance.getType())
  }

  @Test
  fun `createString is idempotent - second call with same name returns the first instance`() {
    val first = AttributeDefinition.createString("attr.idempotent.string", Sensitivity.SAFE, false)
    val second =
      AttributeDefinition.createString("attr.idempotent.string", Sensitivity.HIGHLY_SENSITIVE, true)
    assertSame(first, second)
  }

  @Test
  fun `createString stores the provided sensitivity`() {
    val instance =
      AttributeDefinition.createString("attr.sensitive.string", Sensitivity.HIGHLY_SENSITIVE, false)
    assertEquals(Sensitivity.HIGHLY_SENSITIVE, instance.getSensitivity())
  }

  @Test
  fun `createString accepts isRequiredForContext without error`() {
    val instance = AttributeDefinition.createString("attr.context.required", Sensitivity.SAFE, true)
    assertEquals("attr.context.required", instance.getKey())
  }
}
