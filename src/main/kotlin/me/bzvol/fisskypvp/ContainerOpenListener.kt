package me.bzvol.fisskypvp

import me.bzvol.fisskypvp.FisSkyPVP.Companion.sendPrefixedMessage
import me.bzvol.fisskypvp.config.BaseConfigManager
import me.bzvol.fisskypvp.loot.LootController
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.roundToInt

class ContainerOpenListener : Listener {
    @EventHandler
    fun onContainerOpen(event: PlayerInteractEvent) {
        if (!event.action.isRightClick || event.clickedBlock?.state !is Container)
            return

        val block = event.clickedBlock!!

        val loot = LootController.loot(block.location) ?: return

        val container = block.state as Container
        val inventory = container.snapshotInventory
        inventory.clear()

        val testMode = BaseConfigManager.testmodePlayers.contains(event.player.uniqueId.toString())

        val cooldown = loot.cooldown * 60 * 1000
        val now = System.currentTimeMillis()
        val lastOpened = loot.lastOpened

        val timeElapsed = now - lastOpened
        if (timeElapsed < cooldown && !testMode) {
            val timeRemaining = cooldown - timeElapsed
            val minutesRemaining = (timeRemaining / 100.0 / 60.0).roundToInt() / 10.0
            event.player.sendPrefixedMessage("§eYou can open this loot in $minutesRemaining minutes.")

            event.isCancelled = true
            return
        }

        val slotIterator = getArrangement(loot.items.size).iterator()
        loot.items.forEach {
            inventory.setItem(slotIterator.next(), it)
        }

        container.update(true)

        if (!testMode) loot.lastOpened = now
        LootController.addOrUpdate(loot)
    }

    companion object {
        // Nice arrangement of slots
        private val allowedCounts = setOf(1, 3, 5, 9, 11, 15, 17, 21, 23)
        private fun getArrangement(count: Int): Iterable<Int> {
            if (count == 1) return listOf(13)
            if (count == 3) return listOf(12, 13, 14)

            if (count !in allowedCounts)
                return 0..<count

            val slots = mutableSetOf(4, 12, 13, 14, 22)

            var (lastIndex1, lastIndex2) = Pair(12, 14)
            var nextArrangement = 1 // 0 = vertical, 1 = horizontal

            var i = 0
            while (i < count - 5) {
                slots.addAll(
                    if (nextArrangement == 0) listOf(--lastIndex1, ++lastIndex2)
                    else listOf(lastIndex1 - 9, lastIndex1 + 9, lastIndex2 - 9, lastIndex2 + 9)
                )
                i += if (nextArrangement == 0) 2 else 4
                nextArrangement = (nextArrangement + 1) % 2
            }

            return slots.sorted()
        }
    }
}