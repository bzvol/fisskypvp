package me.bzvol.fisskypvp.config

import me.bzvol.fisskypvp.FisSkyPVP

object BaseConfigManager {
    private val config = FisSkyPVP.instance.config

    /* Plugin prefix is available in FGsSkyPVP.PREFIX as a constant - thus it's also uneditable */

    var testmodePlayers: List<String>
        get() = config.getStringList("testmode-players")
        set(value) = setAndReload("testmode-players", value)

    fun init() {
        config.addDefault("plugin-prefix", FisSkyPVP.PREFIX)
        config.addDefault("testmode-players", emptyList<String>())

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