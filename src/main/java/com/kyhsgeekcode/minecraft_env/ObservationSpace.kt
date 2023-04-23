package com.kyhsgeekcode.minecraft_env

import net.minecraft.item.Item

data class ItemStack(
    val rawId: Int,
    val translationKey: String,
    val count: Int,
    val durability: Int,
    val maxDurability: Int
) {
    constructor(itemStack: net.minecraft.item.ItemStack) : this(
        Item.getRawId(itemStack.item),
        itemStack.item.translationKey,
        itemStack.count,
        itemStack.maxDamage - itemStack.damage,
        itemStack.maxDamage
    )
}

data class ObservationSpace(
    val image: String = "",
    val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0,
    val yaw: Double = 0.0, val pitch: Double = 0.0,
    val health: Double = 20.0,
    val foodLevel: Double = 20.0,
    val saturationLevel: Double = 0.0,
    val isDead: Boolean = false,
    val inventory: List<ItemStack> = listOf(),
)