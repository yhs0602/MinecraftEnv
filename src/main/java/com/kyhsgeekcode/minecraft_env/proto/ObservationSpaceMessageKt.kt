//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: observation_space.proto

package com.kyhsgeekcode.minecraft_env.proto;

@kotlin.jvm.JvmName("-initializeobservationSpaceMessage")
public inline fun observationSpaceMessage(block: com.kyhsgeekcode.minecraft_env.proto.ObservationSpaceMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage =
  com.kyhsgeekcode.minecraft_env.proto.ObservationSpaceMessageKt.Dsl._create(com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage.newBuilder()).apply { block() }._build()
public object ObservationSpaceMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage = _builder.build()

    /**
     * <code>bytes image = 1;</code>
     */
    public var image: com.google.protobuf.ByteString
      @JvmName("getImage")
      get() = _builder.getImage()
      @JvmName("setImage")
      set(value) {
        _builder.setImage(value)
      }
    /**
     * <code>bytes image = 1;</code>
     */
    public fun clearImage() {
      _builder.clearImage()
    }

    /**
     * <code>double x = 2;</code>
     */
    public var x: kotlin.Double
      @JvmName("getX")
      get() = _builder.getX()
      @JvmName("setX")
      set(value) {
        _builder.setX(value)
      }
    /**
     * <code>double x = 2;</code>
     */
    public fun clearX() {
      _builder.clearX()
    }

    /**
     * <code>double y = 3;</code>
     */
    public var y: kotlin.Double
      @JvmName("getY")
      get() = _builder.getY()
      @JvmName("setY")
      set(value) {
        _builder.setY(value)
      }
    /**
     * <code>double y = 3;</code>
     */
    public fun clearY() {
      _builder.clearY()
    }

    /**
     * <code>double z = 4;</code>
     */
    public var z: kotlin.Double
      @JvmName("getZ")
      get() = _builder.getZ()
      @JvmName("setZ")
      set(value) {
        _builder.setZ(value)
      }
    /**
     * <code>double z = 4;</code>
     */
    public fun clearZ() {
      _builder.clearZ()
    }

    /**
     * <code>double yaw = 5;</code>
     */
    public var yaw: kotlin.Double
      @JvmName("getYaw")
      get() = _builder.getYaw()
      @JvmName("setYaw")
      set(value) {
        _builder.setYaw(value)
      }
    /**
     * <code>double yaw = 5;</code>
     */
    public fun clearYaw() {
      _builder.clearYaw()
    }

    /**
     * <code>double pitch = 6;</code>
     */
    public var pitch: kotlin.Double
      @JvmName("getPitch")
      get() = _builder.getPitch()
      @JvmName("setPitch")
      set(value) {
        _builder.setPitch(value)
      }
    /**
     * <code>double pitch = 6;</code>
     */
    public fun clearPitch() {
      _builder.clearPitch()
    }

    /**
     * <code>double health = 7;</code>
     */
    public var health: kotlin.Double
      @JvmName("getHealth")
      get() = _builder.getHealth()
      @JvmName("setHealth")
      set(value) {
        _builder.setHealth(value)
      }
    /**
     * <code>double health = 7;</code>
     */
    public fun clearHealth() {
      _builder.clearHealth()
    }

    /**
     * <code>double food_level = 8;</code>
     */
    public var foodLevel: kotlin.Double
      @JvmName("getFoodLevel")
      get() = _builder.getFoodLevel()
      @JvmName("setFoodLevel")
      set(value) {
        _builder.setFoodLevel(value)
      }
    /**
     * <code>double food_level = 8;</code>
     */
    public fun clearFoodLevel() {
      _builder.clearFoodLevel()
    }

    /**
     * <code>double saturation_level = 9;</code>
     */
    public var saturationLevel: kotlin.Double
      @JvmName("getSaturationLevel")
      get() = _builder.getSaturationLevel()
      @JvmName("setSaturationLevel")
      set(value) {
        _builder.setSaturationLevel(value)
      }
    /**
     * <code>double saturation_level = 9;</code>
     */
    public fun clearSaturationLevel() {
      _builder.clearSaturationLevel()
    }

    /**
     * <code>bool is_dead = 10;</code>
     */
    public var isDead: kotlin.Boolean
      @JvmName("getIsDead")
      get() = _builder.getIsDead()
      @JvmName("setIsDead")
      set(value) {
        _builder.setIsDead(value)
      }
    /**
     * <code>bool is_dead = 10;</code>
     */
    public fun clearIsDead() {
      _builder.clearIsDead()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class InventoryProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     */
     public val inventory: com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getInventoryList()
      )
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     * @param value The inventory to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addInventory")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.add(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack) {
      _builder.addInventory(value)
    }
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     * @param value The inventory to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignInventory")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.plusAssign(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack) {
      add(value)
    }
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     * @param values The inventory to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllInventory")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.addAll(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack>) {
      _builder.addAllInventory(values)
    }
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     * @param values The inventory to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllInventory")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.plusAssign(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack>) {
      addAll(values)
    }
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     * @param index The index to set the value at.
     * @param value The inventory to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setInventory")
    public operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.set(index: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack) {
      _builder.setInventory(index, value)
    }
    /**
     * <code>repeated .ItemStack inventory = 11;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearInventory")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ItemStack, InventoryProxy>.clear() {
      _builder.clearInventory()
    }


    /**
     * <code>.HitResult raycast_result = 12;</code>
     */
    public var raycastResult: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.HitResult
      @JvmName("getRaycastResult")
      get() = _builder.getRaycastResult()
      @JvmName("setRaycastResult")
      set(value) {
        _builder.setRaycastResult(value)
      }
    /**
     * <code>.HitResult raycast_result = 12;</code>
     */
    public fun clearRaycastResult() {
      _builder.clearRaycastResult()
    }
    /**
     * <code>.HitResult raycast_result = 12;</code>
     * @return Whether the raycastResult field is set.
     */
    public fun hasRaycastResult(): kotlin.Boolean {
      return _builder.hasRaycastResult()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class SoundSubtitlesProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     */
     public val soundSubtitles: com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getSoundSubtitlesList()
      )
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     * @param value The soundSubtitles to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addSoundSubtitles")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.add(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry) {
      _builder.addSoundSubtitles(value)
    }
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     * @param value The soundSubtitles to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignSoundSubtitles")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.plusAssign(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry) {
      add(value)
    }
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     * @param values The soundSubtitles to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllSoundSubtitles")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.addAll(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry>) {
      _builder.addAllSoundSubtitles(values)
    }
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     * @param values The soundSubtitles to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllSoundSubtitles")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.plusAssign(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry>) {
      addAll(values)
    }
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     * @param index The index to set the value at.
     * @param value The soundSubtitles to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setSoundSubtitles")
    public operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.set(index: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry) {
      _builder.setSoundSubtitles(index, value)
    }
    /**
     * <code>repeated .SoundEntry sound_subtitles = 13;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearSoundSubtitles")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.SoundEntry, SoundSubtitlesProxy>.clear() {
      _builder.clearSoundSubtitles()
    }


    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class StatusEffectsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     */
     public val statusEffects: com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getStatusEffectsList()
      )
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     * @param value The statusEffects to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addStatusEffects")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.add(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect) {
      _builder.addStatusEffects(value)
    }
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     * @param value The statusEffects to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignStatusEffects")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.plusAssign(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect) {
      add(value)
    }
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     * @param values The statusEffects to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllStatusEffects")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.addAll(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect>) {
      _builder.addAllStatusEffects(values)
    }
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     * @param values The statusEffects to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllStatusEffects")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.plusAssign(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect>) {
      addAll(values)
    }
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     * @param index The index to set the value at.
     * @param value The statusEffects to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setStatusEffects")
    public operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.set(index: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect) {
      _builder.setStatusEffects(index, value)
    }
    /**
     * <code>repeated .StatusEffect status_effects = 14;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearStatusEffects")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.StatusEffect, StatusEffectsProxy>.clear() {
      _builder.clearStatusEffects()
    }


    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class KilledStatisticsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
     public val killedStatistics: com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      @kotlin.jvm.JvmSynthetic
      @JvmName("getKilledStatisticsMap")
      get() = com.google.protobuf.kotlin.DslMap(
        _builder.getKilledStatisticsMap()
      )
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
    @JvmName("putKilledStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      .put(key: kotlin.String, value: kotlin.Int) {
         _builder.putKilledStatistics(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("setKilledStatistics")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      .set(key: kotlin.String, value: kotlin.Int) {
         put(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("removeKilledStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      .remove(key: kotlin.String) {
         _builder.removeKilledStatistics(key)
       }
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("putAllKilledStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      .putAll(map: kotlin.collections.Map<kotlin.String, kotlin.Int>) {
         _builder.putAllKilledStatistics(map)
       }
    /**
     * <code>map&lt;string, int32&gt; killed_statistics = 15;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("clearKilledStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, KilledStatisticsProxy>
      .clear() {
         _builder.clearKilledStatistics()
       }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class MinedStatisticsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
     public val minedStatistics: com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      @kotlin.jvm.JvmSynthetic
      @JvmName("getMinedStatisticsMap")
      get() = com.google.protobuf.kotlin.DslMap(
        _builder.getMinedStatisticsMap()
      )
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
    @JvmName("putMinedStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      .put(key: kotlin.String, value: kotlin.Int) {
         _builder.putMinedStatistics(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("setMinedStatistics")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      .set(key: kotlin.String, value: kotlin.Int) {
         put(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("removeMinedStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      .remove(key: kotlin.String) {
         _builder.removeMinedStatistics(key)
       }
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("putAllMinedStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      .putAll(map: kotlin.collections.Map<kotlin.String, kotlin.Int>) {
         _builder.putAllMinedStatistics(map)
       }
    /**
     * <code>map&lt;string, int32&gt; mined_statistics = 16;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("clearMinedStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MinedStatisticsProxy>
      .clear() {
         _builder.clearMinedStatistics()
       }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class MiscStatisticsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
     public val miscStatistics: com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      @kotlin.jvm.JvmSynthetic
      @JvmName("getMiscStatisticsMap")
      get() = com.google.protobuf.kotlin.DslMap(
        _builder.getMiscStatisticsMap()
      )
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
    @JvmName("putMiscStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      .put(key: kotlin.String, value: kotlin.Int) {
         _builder.putMiscStatistics(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("setMiscStatistics")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      .set(key: kotlin.String, value: kotlin.Int) {
         put(key, value)
       }
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("removeMiscStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      .remove(key: kotlin.String) {
         _builder.removeMiscStatistics(key)
       }
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("putAllMiscStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      .putAll(map: kotlin.collections.Map<kotlin.String, kotlin.Int>) {
         _builder.putAllMiscStatistics(map)
       }
    /**
     * <code>map&lt;string, int32&gt; misc_statistics = 17;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("clearMiscStatistics")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.Int, MiscStatisticsProxy>
      .clear() {
         _builder.clearMiscStatistics()
       }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class VisibleEntitiesProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     */
     public val visibleEntities: com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getVisibleEntitiesList()
      )
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     * @param value The visibleEntities to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addVisibleEntities")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.add(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo) {
      _builder.addVisibleEntities(value)
    }
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     * @param value The visibleEntities to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignVisibleEntities")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.plusAssign(value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo) {
      add(value)
    }
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     * @param values The visibleEntities to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllVisibleEntities")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.addAll(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo>) {
      _builder.addAllVisibleEntities(values)
    }
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     * @param values The visibleEntities to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllVisibleEntities")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.plusAssign(values: kotlin.collections.Iterable<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo>) {
      addAll(values)
    }
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     * @param index The index to set the value at.
     * @param value The visibleEntities to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setVisibleEntities")
    public operator fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.set(index: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo) {
      _builder.setVisibleEntities(index, value)
    }
    /**
     * <code>repeated .EntityInfo visible_entities = 18;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearVisibleEntities")
    public fun com.google.protobuf.kotlin.DslList<com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntityInfo, VisibleEntitiesProxy>.clear() {
      _builder.clearVisibleEntities()
    }


    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class SurroundingEntitiesProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
     public val surroundingEntities: com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      @kotlin.jvm.JvmSynthetic
      @JvmName("getSurroundingEntitiesMap")
      get() = com.google.protobuf.kotlin.DslMap(
        _builder.getSurroundingEntitiesMap()
      )
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
    @JvmName("putSurroundingEntities")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      .put(key: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance) {
         _builder.putSurroundingEntities(key, value)
       }
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("setSurroundingEntities")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      .set(key: kotlin.Int, value: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance) {
         put(key, value)
       }
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("removeSurroundingEntities")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      .remove(key: kotlin.Int) {
         _builder.removeSurroundingEntities(key)
       }
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("putAllSurroundingEntities")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      .putAll(map: kotlin.collections.Map<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance>) {
         _builder.putAllSurroundingEntities(map)
       }
    /**
     * <code>map&lt;int32, .EntitiesWithinDistance&gt; surrounding_entities = 19;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("clearSurroundingEntities")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.Int, com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.EntitiesWithinDistance, SurroundingEntitiesProxy>
      .clear() {
         _builder.clearSurroundingEntities()
       }

    /**
     * <code>bool bobber_thrown = 20;</code>
     */
    public var bobberThrown: kotlin.Boolean
      @JvmName("getBobberThrown")
      get() = _builder.getBobberThrown()
      @JvmName("setBobberThrown")
      set(value) {
        _builder.setBobberThrown(value)
      }
    /**
     * <code>bool bobber_thrown = 20;</code>
     */
    public fun clearBobberThrown() {
      _builder.clearBobberThrown()
    }

    /**
     * <code>int32 experience = 21;</code>
     */
    public var experience: kotlin.Int
      @JvmName("getExperience")
      get() = _builder.getExperience()
      @JvmName("setExperience")
      set(value) {
        _builder.setExperience(value)
      }
    /**
     * <code>int32 experience = 21;</code>
     */
    public fun clearExperience() {
      _builder.clearExperience()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage.copy(block: com.kyhsgeekcode.minecraft_env.proto.ObservationSpaceMessageKt.Dsl.() -> kotlin.Unit): com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessage =
  com.kyhsgeekcode.minecraft_env.proto.ObservationSpaceMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.ObservationSpaceMessageOrBuilder.raycastResultOrNull: com.kyhsgeekcode.minecraft_env.proto.ObservationSpace.HitResult?
  get() = if (hasRaycastResult()) getRaycastResult() else null

