package me.bzvol.fisskypvp

import me.bzvol.fisskypvp.command.MainCommand
import me.bzvol.fisskypvp.config.BaseConfigManager
import me.bzvol.fisskypvp.config.LootConfigManager
import me.bzvol.paperelevate.Util.registerAllCommands
import me.bzvol.paperelevate.Util.registerAllListeners
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class FisSkyPVP : JavaPlugin() {
    override fun onEnable() {
        instance = this
        Companion.logger = this.logger

        BaseConfigManager.init()
        LootConfigManager.init()

        registerAllCommands(
            MainCommand(),
        )

        registerAllListeners(
            ContainerOpenListener(),
        )
    }

    companion object {
        const val PREFIX = "§8[§bFisSkyPVP§8] §r"
        lateinit var instance: FisSkyPVP
        lateinit var logger: Logger

        fun CommandSender.sendPrefixedMessage(message: String) = sendMessage(PREFIX + message)

        fun handleException(sender: CommandSender, usage: String? = null, function: () -> Unit) {
            try {
                function()
            } catch (e: Exception) {
                logger.severe("Error thrown for ${sender.name}: ${e.stackTraceToString()}")
                sender.sendPrefixedMessage("§c${e.message}")
                if (usage != null) sender.sendMessage(usage)
            }
        }
    }
}