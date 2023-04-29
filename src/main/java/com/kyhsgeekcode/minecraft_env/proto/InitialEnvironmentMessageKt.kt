//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: initial_environment.proto

package com.kyhsgeekcode.minecraft_env.proto;

@kotlin.jvm.JvmName("-initializeinitialEnvironmentMessage")
public inline fun initialEnvironmentMessage(block: com.kyhsgeekcode.minecraft_env.proto.InitialEnvironmentMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage =
  com.kyhsgeekcode.minecraft_env.proto.InitialEnvironmentMessageKt.Dsl._create(com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage.newBuilder()).apply { block() }._build()
public object InitialEnvironmentMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage = _builder.build()

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class InitialInventoryCommandsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @return A list containing the initialInventoryCommands.
     */
    public val initialInventoryCommands: com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getInitialInventoryCommandsList()
      )
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @param value The initialInventoryCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addInitialInventoryCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.add(value: kotlin.String) {
      _builder.addInitialInventoryCommands(value)
    }
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @param value The initialInventoryCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignInitialInventoryCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.plusAssign(value: kotlin.String) {
      add(value)
    }
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @param values The initialInventoryCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllInitialInventoryCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.addAll(values: kotlin.collections.Iterable<kotlin.String>) {
      _builder.addAllInitialInventoryCommands(values)
    }
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @param values The initialInventoryCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllInitialInventoryCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.String>) {
      addAll(values)
    }
    /**
     * <code>repeated string initialInventoryCommands = 1;</code>
     * @param index The index to set the value at.
     * @param value The initialInventoryCommands to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setInitialInventoryCommands")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.set(index: kotlin.Int, value: kotlin.String) {
      _builder.setInitialInventoryCommands(index, value)
    }/**
     * <code>repeated string initialInventoryCommands = 1;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearInitialInventoryCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialInventoryCommandsProxy>.clear() {
      _builder.clearInitialInventoryCommands()
    }
    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class InitialPositionProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated int32 initialPosition = 2;</code>
     */
     public val initialPosition: com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getInitialPositionList()
      )
    /**
     * <code>repeated int32 initialPosition = 2;</code>
     * @param value The initialPosition to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addInitialPosition")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.add(value: kotlin.Int) {
      _builder.addInitialPosition(value)
    }/**
     * <code>repeated int32 initialPosition = 2;</code>
     * @param value The initialPosition to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignInitialPosition")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.plusAssign(value: kotlin.Int) {
      add(value)
    }/**
     * <code>repeated int32 initialPosition = 2;</code>
     * @param values The initialPosition to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllInitialPosition")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.addAll(values: kotlin.collections.Iterable<kotlin.Int>) {
      _builder.addAllInitialPosition(values)
    }/**
     * <code>repeated int32 initialPosition = 2;</code>
     * @param values The initialPosition to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllInitialPosition")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.Int>) {
      addAll(values)
    }/**
     * <code>repeated int32 initialPosition = 2;</code>
     * @param index The index to set the value at.
     * @param value The initialPosition to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setInitialPosition")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.set(index: kotlin.Int, value: kotlin.Int) {
      _builder.setInitialPosition(index, value)
    }/**
     * <code>repeated int32 initialPosition = 2;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearInitialPosition")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, InitialPositionProxy>.clear() {
      _builder.clearInitialPosition()
    }
    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class InitialMobsCommandsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @return A list containing the initialMobsCommands.
     */
    public val initialMobsCommands: com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getInitialMobsCommandsList()
      )
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @param value The initialMobsCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addInitialMobsCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.add(value: kotlin.String) {
      _builder.addInitialMobsCommands(value)
    }
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @param value The initialMobsCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignInitialMobsCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.plusAssign(value: kotlin.String) {
      add(value)
    }
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @param values The initialMobsCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllInitialMobsCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.addAll(values: kotlin.collections.Iterable<kotlin.String>) {
      _builder.addAllInitialMobsCommands(values)
    }
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @param values The initialMobsCommands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllInitialMobsCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.String>) {
      addAll(values)
    }
    /**
     * <code>repeated string initialMobsCommands = 3;</code>
     * @param index The index to set the value at.
     * @param value The initialMobsCommands to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setInitialMobsCommands")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.set(index: kotlin.Int, value: kotlin.String) {
      _builder.setInitialMobsCommands(index, value)
    }/**
     * <code>repeated string initialMobsCommands = 3;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearInitialMobsCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, InitialMobsCommandsProxy>.clear() {
      _builder.clearInitialMobsCommands()
    }
    /**
     * <code>int32 imageSizeX = 4;</code>
     */
    public var imageSizeX: kotlin.Int
      @JvmName("getImageSizeX")
      get() = _builder.getImageSizeX()
      @JvmName("setImageSizeX")
      set(value) {
        _builder.setImageSizeX(value)
      }
    /**
     * <code>int32 imageSizeX = 4;</code>
     */
    public fun clearImageSizeX() {
      _builder.clearImageSizeX()
    }

    /**
     * <code>int32 imageSizeY = 5;</code>
     */
    public var imageSizeY: kotlin.Int
      @JvmName("getImageSizeY")
      get() = _builder.getImageSizeY()
      @JvmName("setImageSizeY")
      set(value) {
        _builder.setImageSizeY(value)
      }
    /**
     * <code>int32 imageSizeY = 5;</code>
     */
    public fun clearImageSizeY() {
      _builder.clearImageSizeY()
    }

    /**
     * <code>int64 seed = 6;</code>
     */
    public var seed: kotlin.Long
      @JvmName("getSeed")
      get() = _builder.getSeed()
      @JvmName("setSeed")
      set(value) {
        _builder.setSeed(value)
      }
    /**
     * <code>int64 seed = 6;</code>
     */
    public fun clearSeed() {
      _builder.clearSeed()
    }

    /**
     * <code>bool allowMobSpawn = 7;</code>
     */
    public var allowMobSpawn: kotlin.Boolean
      @JvmName("getAllowMobSpawn")
      get() = _builder.getAllowMobSpawn()
      @JvmName("setAllowMobSpawn")
      set(value) {
        _builder.setAllowMobSpawn(value)
      }
    /**
     * <code>bool allowMobSpawn = 7;</code>
     */
    public fun clearAllowMobSpawn() {
      _builder.clearAllowMobSpawn()
    }

    /**
     * <code>bool alwaysNight = 8;</code>
     */
    public var alwaysNight: kotlin.Boolean
      @JvmName("getAlwaysNight")
      get() = _builder.getAlwaysNight()
      @JvmName("setAlwaysNight")
      set(value) {
        _builder.setAlwaysNight(value)
      }
    /**
     * <code>bool alwaysNight = 8;</code>
     */
    public fun clearAlwaysNight() {
      _builder.clearAlwaysNight()
    }

    /**
     * <code>bool alwaysDay = 9;</code>
     */
    public var alwaysDay: kotlin.Boolean
      @JvmName("getAlwaysDay")
      get() = _builder.getAlwaysDay()
      @JvmName("setAlwaysDay")
      set(value) {
        _builder.setAlwaysDay(value)
      }
    /**
     * <code>bool alwaysDay = 9;</code>
     */
    public fun clearAlwaysDay() {
      _builder.clearAlwaysDay()
    }

    /**
     * <code>string initialWeather = 10;</code>
     */
    public var initialWeather: kotlin.String
      @JvmName("getInitialWeather")
      get() = _builder.getInitialWeather()
      @JvmName("setInitialWeather")
      set(value) {
        _builder.setInitialWeather(value)
      }
    /**
     * <code>string initialWeather = 10;</code>
     */
    public fun clearInitialWeather() {
      _builder.clearInitialWeather()
    }

    /**
     * <code>bool isWorldFlat = 11;</code>
     */
    public var isWorldFlat: kotlin.Boolean
      @JvmName("getIsWorldFlat")
      get() = _builder.getIsWorldFlat()
      @JvmName("setIsWorldFlat")
      set(value) {
        _builder.setIsWorldFlat(value)
      }
    /**
     * <code>bool isWorldFlat = 11;</code>
     */
    public fun clearIsWorldFlat() {
      _builder.clearIsWorldFlat()
    }

    /**
     * <code>int32 visibleSizeX = 12;</code>
     */
    public var visibleSizeX: kotlin.Int
      @JvmName("getVisibleSizeX")
      get() = _builder.getVisibleSizeX()
      @JvmName("setVisibleSizeX")
      set(value) {
        _builder.setVisibleSizeX(value)
      }
    /**
     * <code>int32 visibleSizeX = 12;</code>
     */
    public fun clearVisibleSizeX() {
      _builder.clearVisibleSizeX()
    }

    /**
     * <code>int32 visibleSizeY = 13;</code>
     */
    public var visibleSizeY: kotlin.Int
      @JvmName("getVisibleSizeY")
      get() = _builder.getVisibleSizeY()
      @JvmName("setVisibleSizeY")
      set(value) {
        _builder.setVisibleSizeY(value)
      }
    /**
     * <code>int32 visibleSizeY = 13;</code>
     */
    public fun clearVisibleSizeY() {
      _builder.clearVisibleSizeY()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage.copy(block: com.kyhsgeekcode.minecraft_env.proto.InitialEnvironmentMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.InitialEnvironment.InitialEnvironmentMessage =
  com.kyhsgeekcode.minecraft_env.proto.InitialEnvironmentMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()
