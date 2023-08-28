package me.bzvol.fisskypvp.config

import me.bzvol.fisskypvp.FisSkyPVP

object BaseConfigManager {
    private val config = FisSkyPVP.instance.config

    /* Plugin prefix is available in FGsSkyPVP.PREFIX as a constant - thus it's also uneditable */

    fun init() {
        config.addDefault("default-cooldown", 60)
        config.addDefault("plugin-prefix", FisSkyPVP.PREFIX)

        config.options().copyDefaults(true)
        FisSkyPVP.instance.saveDefaultConfig()
    }

    fun save() = FisSkyPVP.instance.saveConfig()
    fun reload() = FisSkyPVP.instance.reloadConfig()

    private fun setAndReload(key: String, value: Any) {
        config.set(key, value)
        save()
        reload()
    }
}