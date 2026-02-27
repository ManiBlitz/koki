package com.smallee.metrics

import com.smallee.attributes.AttributeDefinition
import com.smallee.attributes.AttributeEntry
import com.smallee.attributes.Sensitivity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MetricBuilderTest {

  private val tagA = AttributeDefinition.createString("mbt.tag.a", Sensitivity.SAFE, false)
  private val tagB = AttributeDefinition.createLong("mbt.tag.b", Sensitivity.SAFE, false)

  /** Captures every argument the factory receives when [MetricBuilder.build] is called. */
  private data class Capture(
    val description: String,
    val unit: String,
    val baseTags: List<AttributeEntry<*>>,
  )

  private fun capturingBuilder(): Pair<MetricBuilder<Capture>, () -> Capture> {
    lateinit var captured: Capture
    val builder = MetricBuilder { description, unit, baseTags ->
      Capture(description, unit, baseTags).also { captured = it }
    }
    return builder to { captured }
  }

  // ── description ────────────────────────────────────────────────────────────

  @Test
  fun `description is forwarded to the factory on build`() {
    val (builder, result) = capturingBuilder()
    builder.description("Total HTTP requests").build()
    assertEquals("Total HTTP requests", result().description)
  }

  @Test
  fun `default description is empty when not set`() {
    val (builder, result) = capturingBuilder()
    builder.build()
    assertEquals("", result().description)
  }

  // ── unit ───────────────────────────────────────────────────────────────────

  @Test
  fun `unit is forwarded to the factory on build`() {
    val (builder, result) = capturingBuilder()
    builder.unit("ms").build()
    assertEquals("ms", result().unit)
  }

  @Test
  fun `default unit is empty when not set`() {
    val (builder, result) = capturingBuilder()
    builder.build()
    assertEquals("", result().unit)
  }

  // ── addTag ─────────────────────────────────────────────────────────────────

  @Test
  fun `addTag stores an entry with the correct definition and value`() {
    val (builder, result) = capturingBuilder()
    builder.addTag(tagA, "eu-west-1").build()
    val tags = result().baseTags
    assertEquals(1, tags.size)
    assertSame(tagA, tags[0].definition)
    assertEquals("eu-west-1", tags[0].value)
  }

  @Test
  fun `addTag accumulates multiple calls in order`() {
    val (builder, result) = capturingBuilder()
    builder.addTag(tagA, "first").addTag(tagA, "second").build()
    val tags = result().baseTags
    assertEquals(2, tags.size)
    assertEquals("first", tags[0].value)
    assertEquals("second", tags[1].value)
  }

  // ── addTagEntry ────────────────────────────────────────────────────────────

  @Test
  fun `addTagEntry stores the entry as-is`() {
    val (builder, result) = capturingBuilder()
    val entry = AttributeEntry(tagA, "direct")
    builder.addTagEntry(entry).build()
    val tags = result().baseTags
    assertEquals(1, tags.size)
    assertEquals("direct", tags[0].value)
  }

  @Test
  fun `addTagEntry and addTag can be mixed`() {
    val (builder, result) = capturingBuilder()
    builder.addTag(tagA, "from-addTag").addTagEntry(AttributeEntry(tagB, 42L)).build()
    val tags = result().baseTags
    assertEquals(2, tags.size)
    assertEquals("from-addTag", tags[0].value)
    assertEquals(42L, tags[1].value)
  }

  // ── addAllTags ─────────────────────────────────────────────────────────────

  @Test
  fun `addAllTags stores all entries from the list`() {
    val (builder, result) = capturingBuilder()
    builder.addAllTags(listOf(AttributeEntry(tagA, "region"), AttributeEntry(tagB, 99L))).build()
    val tags = result().baseTags
    assertEquals(2, tags.size)
    assertEquals("region", tags[0].value)
    assertEquals(99L, tags[1].value)
  }

  @Test
  fun `addAllTags with an empty list adds no tags`() {
    val (builder, result) = capturingBuilder()
    builder.addAllTags(emptyList()).build()
    assertEquals(0, result().baseTags.size)
  }

  // ── chaining ───────────────────────────────────────────────────────────────

  @Test
  fun `description returns the same builder instance`() {
    val builder = MetricBuilder<Unit> { _, _, _ -> }
    assertSame(builder, builder.description("x"))
  }

  @Test
  fun `unit returns the same builder instance`() {
    val builder = MetricBuilder<Unit> { _, _, _ -> }
    assertSame(builder, builder.unit("ms"))
  }

  @Test
  fun `addTag returns the same builder instance`() {
    val builder = MetricBuilder<Unit> { _, _, _ -> }
    assertSame(builder, builder.addTag(tagA, "v"))
  }

  @Test
  fun `addTagEntry returns the same builder instance`() {
    val builder = MetricBuilder<Unit> { _, _, _ -> }
    assertSame(builder, builder.addTagEntry(AttributeEntry(tagA, "v")))
  }

  @Test
  fun `addAllTags returns the same builder instance`() {
    val builder = MetricBuilder<Unit> { _, _, _ -> }
    assertSame(builder, builder.addAllTags(emptyList()))
  }

  // ── build ──────────────────────────────────────────────────────────────────

  @Test
  fun `build passes the full accumulated configuration to the factory`() {
    val (builder, result) = capturingBuilder()
    builder
      .description("Events processed")
      .unit("{event}")
      .addTag(tagA, "sticky")
      .addTagEntry(AttributeEntry(tagB, 7L))
      .build()
    val capture = result()
    assertEquals("Events processed", capture.description)
    assertEquals("{event}", capture.unit)
    assertEquals(2, capture.baseTags.size)
    assertEquals("sticky", capture.baseTags[0].value)
    assertEquals(7L, capture.baseTags[1].value)
  }

  @Test
  fun `baseTags snapshot passed to factory is independent of later builder mutations`() {
    val (builder, result) = capturingBuilder()
    builder.addTag(tagA, "before").build()
    val tagsAtBuildTime = result().baseTags
    // mutate the builder after build — the already-captured snapshot must not change
    builder.addTag(tagA, "after")
    assertEquals(1, tagsAtBuildTime.size)
    assertEquals("before", tagsAtBuildTime[0].value)
  }
}
