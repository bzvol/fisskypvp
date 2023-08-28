package me.bzvol.fisskypvp.loot

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class LootEntry(
    val name: String,
    val blockType: String,
    val location: Location,
    val cooldown: Int,
    var lastOpened: Long,
    val items: List<ItemStack>
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromConfig(lootObj: Map<*, *>): LootEntry = LootEntry(
            lootObj["name"] as String,
            lootObj["blockType"] as String,
            (lootObj["location"] as Map<*, *>).let {
                Location(
                    Bukkit.getWorld(it["world"] as String),
                    (it["posX"] as Int).toDouble(),
                    (it["posY"] as Int).toDouble(),
                    (it["posZ"] as Int).toDouble()
                )
            },
            lootObj["cooldown"] as Int,
            (lootObj["lastOpened"] as String).toLong(),
            (lootObj["items"] as List<Map<String, Any>>).map(ItemStack::deserialize)
        )
    }

    fun toConfig(): Map<String, Any> = mapOf(
        "name" to name,
        "blockType" to blockType,
        "location" to mapOf(
            "world" to location.world.name,
            "posX" to location.blockX,
            "posY" to location.blockY,
            "posZ" to location.blockZ
        ),
        "cooldown" to cooldown,
        "lastOpened" to lastOpened.toString(),
        "items" to items.map(ItemStack::serialize)
    )
}
