package api
//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: ModelAPI.proto

@kotlin.jvm.JvmSynthetic
public inline fun event(block: EventKt.Dsl.() -> kotlin.Unit): ModelAPIOuterClass.Event =
  EventKt.Dsl._create(ModelAPIOuterClass.Event.newBuilder()).apply { block() }._build()
public object EventKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: ModelAPIOuterClass.Event.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: ModelAPIOuterClass.Event.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): ModelAPIOuterClass.Event = _builder.build()
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun ModelAPIOuterClass.Event.copy(block: EventKt.Dsl.() -> kotlin.Unit): ModelAPIOuterClass.Event =
  EventKt.Dsl._create(this.toBuilder()).apply { block() }._build()