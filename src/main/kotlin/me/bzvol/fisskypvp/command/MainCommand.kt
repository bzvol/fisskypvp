package me.bzvol.fisskypvp.command

import me.bzvol.fisskypvp.FisSkyPVP.Companion.sendPrefixedMessage
import me.bzvol.fisskypvp.config.BaseConfigManager
import me.bzvol.fisskypvp.config.LootConfigManager
import me.bzvol.paperelevate.command.Command2
import org.bukkit.command.CommandSender

object MainCommand {
    operator fun invoke() =
        Command2.builder("fsp")
            .subCommand("help") {
                action { sender, _ -> sender.sendUsage() }
            }
            .subCommand("reload") {
                action { sender, _ -> reloadConfig(sender) }
            }
            .subCommand(LootSubCommand())
            .build()

    private fun reloadConfig(sender: CommandSender) {
        BaseConfigManager.reload()
        LootConfigManager.reload()
        sender.sendPrefixedMessage("§aConfig reloaded!")
    }
}