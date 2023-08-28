package me.bzvol.fisskypvp.loot

import me.bzvol.fisskypvp.config.LootConfigManager
import org.bukkit.Location

object LootController {
    fun addOrUpdate(loot: LootEntry) {
        val loots = LootConfigManager.loots.toMutableList()

        loots.removeAll { it.name == loot.name }
        loots.add(loot)

        LootConfigManager.loots = loots
    }

    fun loot(location: Location): LootEntry? = LootConfigManager.loots.find { it.location resembles location }

    fun loot(name: String): LootEntry? = LootConfigManager.loots.find { it.name == name }

    fun delete(location: Location) {
        val loots = LootConfigManager.loots.toMutableList()
        loots.removeAll { it.location resembles location }
        LootConfigManager.loots = loots
    }

    fun delete(name: String) {
        val loots = LootConfigManager.loots.toMutableList()
        loots.removeAll { it.name == name }
        LootConfigManager.loots = loots
    }

    private infix fun Location.resembles(other: Location): Boolean =
        this.world.name == other.world.name && this.blockX == other.blockX
                && this.blockY == other.blockY && this.blockZ == other.blockZ
}