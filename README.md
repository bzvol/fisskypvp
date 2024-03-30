# FisGeng's SkyPVP plugin

## Info

### Description

The plugin uses a simple loot system:

1. You save the chest/container, containing the loot with `/fsp loot save <name> [<cooldown>]`.
2. The loot is now automatically loaded into the chest, and locked until the cooldown is over.
3. Players can now open the chest and get the loot.

### Commands

The plugin uses `/fsp` (`/fisskypvp`) as its main command.

- `/fsp reload` - Reloads all config files
- `/fsp loot`
  - `/fsp loot save <name> [<cooldown>]` - Saves the loot in the chest you're looking at
  - `/fsp loot delete [<name>]` - Deletes the loot entry (or the one you're looking at)
- `/fsp testmode <on/off>` - Toggles test mode, which allows you to open the chest without the cooldown

### Setup

The plugin uses PaperElevate as a dependency, but it's shaded into the plugin jar.<br/>
It also requires the latest Kotlin runtime as a library.
