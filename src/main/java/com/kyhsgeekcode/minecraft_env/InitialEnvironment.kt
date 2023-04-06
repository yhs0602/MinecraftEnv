package com.kyhsgeekcode.minecraft_env

data class InitialEnvironment(
    val initialInventoryCommands: Array<String> = emptyArray(),
    val initialPosition: IntArray? = null,
    val initialMobsCommands: Array<String> = emptyArray(),
    val imageSizeX: Int = 890,
    val imageSizeY: Int = 500,
    val seed: Long? = null,
    val allowMobSpawn: Boolean = true,
    val alwaysNight: Boolean = false,
    val alwaysDay: Boolean = false,
    val initialWeather: String = "clear"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InitialEnvironment

        if (!initialInventoryCommands.contentEquals(other.initialInventoryCommands)) return false
        if (!initialPosition.contentEquals(other.initialPosition)) return false
        if (!initialMobsCommands.contentEquals(other.initialMobsCommands)) return false
        if (imageSizeX != other.imageSizeX) return false
        if (imageSizeY != other.imageSizeY) return false
        if (seed != other.seed) return false
        if (allowMobSpawn != other.allowMobSpawn) return false
        if (alwaysNight != other.alwaysNight) return false
        if (alwaysDay != other.alwaysDay) return false
        if (initialWeather != other.initialWeather) return false

        return true
    }

    override fun hashCode(): Int {
        var result = initialInventoryCommands.contentHashCode()
        result = 31 * result + initialPosition.contentHashCode()
        result = 31 * result + initialMobsCommands.contentHashCode()
        result = 31 * result + imageSizeX
        result = 31 * result + imageSizeY
        result = 31 * result + seed.hashCode()
        result = 31 * result + allowMobSpawn.hashCode()
        result = 31 * result + alwaysNight.hashCode()
        result = 31 * result + alwaysDay.hashCode()
        result = 31 * result + initialWeather.hashCode()
        return result
    }
}