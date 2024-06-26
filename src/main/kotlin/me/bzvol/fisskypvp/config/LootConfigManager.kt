package me.bzvol.fisskypvp.config

import me.bzvol.fisskypvp.FisSkyPVP
import me.bzvol.fisskypvp.loot.LootEntry
import me.bzvol.paperelevate.config.Config2

object LootConfigManager : Config2(FisSkyPVP.instance, "loots") {
    var defaultCooldown: Int
        get() = config.getInt("default-cooldown")
        set(value) = setAndReload("default-cooldown", value)

    var loots: List<LootEntry>
        get() = config.getMapList("loots").map(LootEntry::fromConfig)
        set(value) = setAndReload("loots", value.map(LootEntry::toConfig))

    fun init() = setDefaults(
        "default-cooldown" to 10,
        "loots" to emptyList<Map<String, Any>>()
    )
}