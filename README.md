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

  Not implemented yet:
    - `/fsp loot list` - Lists all loot entries
    - `/fsp loot info <name>` - Shows info about a loot entry
    - `/fsp loot setcooldown <name> <cooldown>` - Sets the cooldown of a loot entry

### Setup

The plugin uses PaperElevate as a dependency, but it's shaded into the plugin jar.<br/>
It also requires the latest Kotlin runtime as a library.

## Development

### Code structure

Loot system currently uses `Loot`, `Loots` and `LootsConfig`.

- `Loot` - Represents a single loot entry
- `Loots` - Represents the controller for `LootsConfig`
- `LootsConfig` - Represents the config file for the loot system,
with automatic parsing and saving of loot lists (and all other config values)

Therefore, the plugin uses some kind of MVC pattern (or rather MC, as there's no view-like thing for a plugin).

The main command's behaviour is defined in `CommonCommand`,
which uses multiple subcommands (currently only one: `LootCommand`).

### TODO

- [x] P1: Fix tab completions
- [x] P1: Change Loot's ItemStack serialization to use its builtin serializer
- [x] P2: Rename commands `save` and `remove` to `add` and `delete`
- [x] P2: Add `/fsp loot info <name>` command to show info about a loot entry
- [x] P2: Add `/fsp loot setcooldown <name> <cooldown>` command to modify the cooldown of a loot entry
- [x] P2: Add `/fsp loot list` command to list all loot entries
- [x] P3: Use SimpleArgParser instead of deprecated ArgParser, where applicable
- [x] P3: Rename classes (implement a better naming scheme)
- [x] P3: Add `/fsp loot tp <name>` command to teleport to the loot entry
- [ ] P2: Pagination for `/fsp loot list`
- [ ] P2: Add loot groups
  - [ ] Add groups in config
  - [ ] Add groups in loot entries
  - [ ] Add `/fsp loot setgroup <name> <group>` command to set the group of a loot entry
  - [ ] Add `/fsp group` commands to manage groups

#### PaperElevate (an add-on for PaperMC plugins)
- [x] P2: Implement `SimpleArgParser` and `FlaggedArgParser` classes
- [x] P2: Add automatic usages and tab completions for ArgParser(s)
- [x] P3: Implement `subCommandTabCompletions` for commands
- [x] P2: Make allowed values providable
- [ ] P1: Permissions (for subcommands)
- [ ] P2: Implement `MixedArgParser` class
- [ ] P2: Add allowed values to tab completions
- [ ] P2: Allow subcommands and default action with parsed args at the same time
- [ ] P3: Implement config file abstraction (+`setDefaults`)