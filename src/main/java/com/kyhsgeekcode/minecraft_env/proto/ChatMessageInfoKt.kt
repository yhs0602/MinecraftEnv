// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: observation_space.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package com.kyhsgeekcode.minecraft_env.proto;

@kotlin.jvm.JvmName("-initializechatMessageInfo")
public inline fun chatMessageInfo(block: com.kyhsgeekcode.minecraft_env.proto.ChatMessageInfoKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo =
  com.kyhsgeekcode.minecraft_env.proto.ChatMessageInfoKt.Dsl._create(com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `ChatMessageInfo`
 */
public object ChatMessageInfoKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo = _builder.build()

    /**
     * `int64 added_time = 1;`
     */
    public var addedTime: kotlin.Long
      @JvmName("getAddedTime")
      get() = _builder.getAddedTime()
      @JvmName("setAddedTime")
      set(value) {
        _builder.setAddedTime(value)
      }
    /**
     * `int64 added_time = 1;`
     */
    public fun clearAddedTime() {
      _builder.clearAddedTime()
    }

    /**
     * `string message = 2;`
     */
    public var message: kotlin.String
      @JvmName("getMessage")
      get() = _builder.getMessage()
      @JvmName("setMessage")
      set(value) {
        _builder.setMessage(value)
      }
    /**
     * `string message = 2;`
     */
    public fun clearMessage() {
      _builder.clearMessage()
    }

    /**
     * ```
     * TODO;; always empty
     * ```
     *
     * `string indicator = 3;`
     */
    public var indicator: kotlin.String
      @JvmName("getIndicator")
      get() = _builder.getIndicator()
      @JvmName("setIndicator")
      set(value) {
        _builder.setIndicator(value)
      }
    /**
     * ```
     * TODO;; always empty
     * ```
     *
     * `string indicator = 3;`
     */
    public fun clearIndicator() {
      _builder.clearIndicator()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo.copy(block: `com.kyhsgeekcode.minecraft_env.proto`.ChatMessageInfoKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ChatMessageInfo =
  `com.kyhsgeekcode.minecraft_env.proto`.ChatMessageInfoKt.Dsl._create(this.toBuilder()).apply { block() }._build()

