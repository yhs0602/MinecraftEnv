// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: action_space.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package com.kyhsgeekcode.minecraft_env.proto;

@kotlin.jvm.JvmName("-initializeactionSpaceMessage")
public inline fun actionSpaceMessage(block: com.kyhsgeekcode.minecraft_env.proto.ActionSpaceMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage =
  com.kyhsgeekcode.minecraft_env.proto.ActionSpaceMessageKt.Dsl._create(com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `ActionSpaceMessage`
 */
public object ActionSpaceMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage = _builder.build()

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class ActionProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated int32 action = 1;`
     */
     public val action: com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getActionList()
      )
    /**
     * `repeated int32 action = 1;`
     * @param value The action to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAction")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.add(value: kotlin.Int) {
      _builder.addAction(value)
    }/**
     * `repeated int32 action = 1;`
     * @param value The action to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAction")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.plusAssign(value: kotlin.Int) {
      add(value)
    }/**
     * `repeated int32 action = 1;`
     * @param values The action to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllAction")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.addAll(values: kotlin.collections.Iterable<kotlin.Int>) {
      _builder.addAllAction(values)
    }/**
     * `repeated int32 action = 1;`
     * @param values The action to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllAction")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.Int>) {
      addAll(values)
    }/**
     * `repeated int32 action = 1;`
     * @param index The index to set the value at.
     * @param value The action to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setAction")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.set(index: kotlin.Int, value: kotlin.Int) {
      _builder.setAction(index, value)
    }/**
     * `repeated int32 action = 1;`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearAction")
    public fun com.google.protobuf.kotlin.DslList<kotlin.Int, ActionProxy>.clear() {
      _builder.clearAction()
    }
    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class CommandsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated string commands = 2;`
     * @return A list containing the commands.
     */
    public val commands: com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getCommandsList()
      )
    /**
     * `repeated string commands = 2;`
     * @param value The commands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.add(value: kotlin.String) {
      _builder.addCommands(value)
    }
    /**
     * `repeated string commands = 2;`
     * @param value The commands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.plusAssign(value: kotlin.String) {
      add(value)
    }
    /**
     * `repeated string commands = 2;`
     * @param values The commands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.addAll(values: kotlin.collections.Iterable<kotlin.String>) {
      _builder.addAllCommands(values)
    }
    /**
     * `repeated string commands = 2;`
     * @param values The commands to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllCommands")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.String>) {
      addAll(values)
    }
    /**
     * `repeated string commands = 2;`
     * @param index The index to set the value at.
     * @param value The commands to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setCommands")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.set(index: kotlin.Int, value: kotlin.String) {
      _builder.setCommands(index, value)
    }/**
     * `repeated string commands = 2;`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearCommands")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, CommandsProxy>.clear() {
      _builder.clearCommands()
    }}
}
@kotlin.jvm.JvmSynthetic
public inline fun com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage.copy(block: `com.kyhsgeekcode.minecraft_env.proto`.ActionSpaceMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ActionSpace.ActionSpaceMessage =
  `com.kyhsgeekcode.minecraft_env.proto`.ActionSpaceMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

