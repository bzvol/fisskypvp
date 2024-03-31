package me.bzvol.fisskypvp.command

import me.bzvol.fisskypvp.FisSkyPVP.Companion.handleException
import me.bzvol.fisskypvp.FisSkyPVP.Companion.sendPrefixedMessage
import me.bzvol.fisskypvp.config.BaseConfigManager
import me.bzvol.fisskypvp.config.LootConfigManager
import me.bzvol.paperelevate.command.Command2
import me.bzvol.paperelevate.command.CommandUtil
import me.bzvol.paperelevate.command.argparser.SimpleArgParser
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MainCommand {
    operator fun invoke() =
        Command2.builder("fsp")
            .permission("fisskypvp.base")
            .subCommand("help") {
                action { sender, _ -> sender.sendUsage() }
            }
            .subCommand("reload") {
                action { sender, _ -> reloadConfig(sender) }
            }
            .subCommand(LootSubCommand())
            .subCommand("testmode", true) {
                argParser(testModeParser)
                permission("fisskypvp.loot")
                action { sender: Player, args -> setTestMode(sender, args) }
            }
            .build()

    private val testModeParser = SimpleArgParser().add<String>("mode",
        allowedValues = { listOf("on", "off") })

    private fun reloadConfig(sender: CommandSender) {
        BaseConfigManager.reload()
        LootConfigManager.reload()
        sender.sendPrefixedMessage("§aConfig reloaded!")
    }

    private fun setTestMode(sender: Player, args: Array<String>) = handleException(
        sender,
        CommandUtil.buildUsage("fsp testmode", emptyList(), testModeParser)
    ) {
        val parsed = testModeParser.parse(args)
        val mode = when (parsed["mode"] as String) {
            "on" -> true
            "off" -> false
            else -> {
                sender.sendPrefixedMessage("§cInvalid mode. Must be 'on' or 'off'.")
                return
            }
        }

        val tmPlayers = BaseConfigManager.testmodePlayers.toMutableSet()
        if (mode)
            if (tmPlayers.add(sender.uniqueId.toString())) {
                BaseConfigManager.testmodePlayers = tmPlayers.toList()
                sender.sendPrefixedMessage("§aTest mode enabled.")
            } else sender.sendPrefixedMessage("§eTest mode already enabled.")
        else
            if (tmPlayers.remove(sender.uniqueId.toString())) {
                BaseConfigManager.testmodePlayers = tmPlayers.toList()
                sender.sendPrefixedMessage("§aTest mode disabled.")
            } else sender.sendPrefixedMessage("§eTest mode is not enabled.")
    }
}