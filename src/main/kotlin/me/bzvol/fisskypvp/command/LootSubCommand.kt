package me.bzvol.fisskypvp.command

import me.bzvol.fisskypvp.FisSkyPVP.Companion.handleException
import me.bzvol.fisskypvp.FisSkyPVP.Companion.sendPrefixedMessage
import me.bzvol.fisskypvp.config.LootConfigManager
import me.bzvol.fisskypvp.loot.LootController
import me.bzvol.fisskypvp.loot.LootEntry
import me.bzvol.paperelevate.Util.closest
import me.bzvol.paperelevate.command.Command2
import me.bzvol.paperelevate.command.CommandUtil
import me.bzvol.paperelevate.command.argparser.SimpleArgParser
import org.bukkit.Location
import org.bukkit.block.Container
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import kotlin.math.ceil

object LootSubCommand {
    private const val LOOTS_PER_PAGE = 5

    operator fun invoke() =
        Command2.builder("loot", true)
            .subCommand("add", true) {
                argParser(addArgParser)
                action { sender: Player, args -> add(sender, args) }
            }
            .subCommand("delete", true) {
                argParser(deleteArgParser)
                action { sender: Player, args -> delete(sender, args) }
            }
            .subCommand("set-cooldown", true) {
                argParser(setCooldownArgParser)
                action { sender: Player, args -> setCooldown(sender, args) }
            }
            .subCommand("list", true) {
                action { sender: Player, args -> list(sender, args) }
            }
            .subCommand("info", true) {
                argParser(infoArgParser)
                action { sender: Player, args -> info(sender, args) }
            }
            .subCommand("tp", true) {
                argParser(tpParser)
                action { sender: Player, args -> tp(sender, args) }
            }
            .build()

    private val addArgParser = SimpleArgParser()
        .add<String>("name").addOptional<Int>("cooldown", LootConfigManager.defaultCooldown)
    private val deleteArgParser = SimpleArgParser().addOptional<String>("name")
    private val listArgParser = SimpleArgParser().addOptional<Int>("page", 1)
    private val setCooldownArgParser = SimpleArgParser().add<Int>("cooldown").addOptional<String>("name")
    private val infoArgParser = SimpleArgParser().addOptional<String>("name")
    private val tpParser = SimpleArgParser().add<String>("name")

    private fun add(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot add", emptyList(), addArgParser)
    ) {
        val parsed = addArgParser.parse(args)
        val name = parsed["name"] as String
        val cooldown = parsed["cooldown"] as Int

        val block = BlockIterator(sender, 10).closest { it.state is Container }
        if (block?.state !is Container) {
            sender.sendPrefixedMessage("§cYou must be looking at a container to save it as a loot chest.")
            return@handleException
        }

        val container = block.state as Container

        val inventory = (block.state as Container).inventory
        val items = inventory.contents.filterNotNull()

        val loot = LootEntry(
            name, container.type.name.lowercase(), container.location,
            cooldown, 0L, items
        )
        LootController.addOrUpdate(loot)

        sender.sendPrefixedMessage("§aSaved loot chest §e$name§a.")
    }

    private fun delete(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot delete", emptyList(), deleteArgParser)
    ) {
        val parsed = deleteArgParser.parse(args)
        val name = parsed["name"] as String

        val loot = getLootByNameOrLook(name, sender)
        if (loot == null) {
            sender.sendPrefixedMessage("§cNo loot chest found.")
            return@handleException
        }

        LootController.delete(loot.name)
        sender.sendPrefixedMessage("§aRemoved loot chest §e${loot.name}§a.")

        val container = loot.location.block.state as Container

        val inventory = container.inventory
        inventory.clear()
        loot.items.forEach { inventory.addItem(it) }

        container.update(true)
    }

    private fun list(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot list", emptyList(), listArgParser)
    ) {
        val parsed = listArgParser.parse(args)
        val page = parsed["page"] as Int

        val loots = LootConfigManager.loots.sortedBy { it.name }
        if (loots.isEmpty()) {
            sender.sendPrefixedMessage("§eThere are no saved loot chests.")
            return@handleException
        }

        val totalPages = ceil(loots.size.toDouble() / LOOTS_PER_PAGE).toInt()
        if (page < 1 || page > totalPages) {
            sender.sendPrefixedMessage("§cInvalid page number. Must be between §b1 §cand §b$totalPages§c.")
            return@handleException
        }

        val start = (page - 1) * LOOTS_PER_PAGE
        val end = (start + LOOTS_PER_PAGE - 1).coerceAtMost(loots.size - 1)
        val pageLoots = loots.slice(start..end)

        sender.sendPrefixedMessage(
            "§aList of saved loots (§b${loots.size}§a in total, page §b$page§a of §b$totalPages§a):\n" +
                    pageLoots.joinToString("\n") {
                        "§7- §e${it.name}§7: cooldown=§3${it.cooldown}§7m, " +
                                "pos=(§3x=${it.location.blockX}§7," +
                                "§3y=${it.location.blockY}§7," +
                                "§3z=${it.location.blockZ}§7), " +
                                "items=§3${it.items.size}"
                    }
        )
    }

    private fun info(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot info", emptyList(), infoArgParser)
    ) {
        val parsed = infoArgParser.parse(args)
        val name = parsed["name"] as String?

        val loot = getLootByNameOrLook(name, sender)
        if (loot == null) {
            sender.sendPrefixedMessage(
                if (name == null) "§cNo loot name specified."
                else "§cNo loot chest found."
            )
            return@handleException
        }

        sender.sendPrefixedMessage(
            "§aLoot chest §e${loot.name}§a:\n" +
                    "§7- cooldown=§3${loot.cooldown}§7m\n" +
                    "§7- pos=(§3x=${loot.location.blockX}§7,§3y=${loot.location.blockY}§7," +
                    "§3z=${loot.location.blockZ}§7)\n" +
                    "§7- items=§3${loot.items.size}"
        )
    }

    private fun setCooldown(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot set-cooldown", emptyList(), setCooldownArgParser)
    ) {
        val parsed = setCooldownArgParser.parse(args)
        val cooldown = parsed["cooldown"] as Int
        val name = parsed["name"] as String?

        val loot = getLootByNameOrLook(name, sender)
        if (loot == null) {
            sender.sendPrefixedMessage("§cNo loot chest found.")
            return@handleException
        }

        LootController.addOrUpdate(loot.copy(cooldown = cooldown))

        sender.sendPrefixedMessage("§aSet cooldown of loot chest §e${loot.name}§a to §3$cooldown§a.")
    }

    private fun tp(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp loot tp", emptyList(), tpParser)
    ) {
        val parsed = tpParser.parse(args)
        val name = parsed["name"] as String

        val loot = LootController.loot(name)
        if (loot == null) {
            sender.sendPrefixedMessage("§cNo loot chest found.")
            return@handleException
        }

        val world = loot.location.world!!
        val x = loot.location.blockX
        val y = loot.location.blockY
        val z = loot.location.blockZ

        val highestY = world.getHighestBlockYAt(x, z) + 1
        val tpLocation = world.getBlockAt(x, highestY, z).location.let {
            Location(it.world, it.x + .5, it.y, it.z + .5)
        }

        sender.teleport(tpLocation)

        val chestDistanceFromTp = highestY - y
        sender.sendPrefixedMessage("§eThe loot chest is §f§l$chestDistanceFromTp§r§e blocks below you.")
    }

    private fun getLootByNameOrLook(name: String?, player: Player, maxDistance: Int = 10): LootEntry? =
        if (!name.isNullOrBlank()) LootController.loot(name)
        else BlockIterator(player, maxDistance).closest { it.state is Container }
            ?.let { LootController.loot(it.location) }
}