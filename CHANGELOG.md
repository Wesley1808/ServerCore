# Changelog

## [1.5.14]

### Fixed

- Fixed a bug with `reduce-sync-loads` where really high-velocity entities weren't detecting collisions correctly, such as enderpearls from pearl cannons.

## [1.5.12]

### Added

- Added `chunk-tick-distance-affects-random-ticks` option to control whether chunk-tick-distance affects random ticks.
  - chunk-tick-distance will still affect mobspawning regardless of this setting.
  - enabling this setting will bring back old behavior, stopping random ticks in chunks too far away from players - even chunks simulated by enderpearl or portal loading.

### Fixed

- Fixed chunk-tick-distance stopping pearl/portal loaded chunks from random ticking (breaking vanilla behavior by default). 
  This old behavior is now hidden behind a config option.

## [1.5.11]

### Added

- Patch to prevent sync loads caused by block entity updates (mostly vaults and trial spawners).

### Fixed

- Ticking chunk cache allowing border chunks to tick (rarely causing a sync load) 

## [1.5.10]

### Fixed

- Mod incompatibility with the latest version of Moonrise.
- Cobblemon mobs despawning much slower when activation range is enabled.

## [1.5.9]

### Added

- Added default configurable values for dynamic settings.

### Fixed

- Fixed not being able to only specify a single dynamic setting in the config.

### Changed

- Chunks in vanilla can now tick if they're loaded by a ticket that allows simulation, not just when players are nearby.
  - Chunk-tick-distance now checks the distance to the nearest simulating ticket instead of the nearest player.
  - Since spawning and block-ticking chunks are now separate, ticking chunk cache now caches both of them.

## [1.5.8]

### Added

- Added the `typeof:projectile` entity matcher for activation range configuration

### Fixed

- Fixed incompatibility between Moonrise and the `enforce-mobcap` settings

## [1.5.7]

### Fixed

- Fixed ender pearl behavior inconsistencies from `prevent-enderpearl-chunkloading`

## [1.5.6]

### Added

- Added `prevent-enderpearl-chunkloading`, an option to disable enderpearl chunkloading from 1.21.2 (reverts behavior to
  pre-1.21.2)

### Fixed

- Improved mod compatibility with mods that initialize entities extremely early.

## [1.5.5]

### Added

- Config option for unlimited height in breeding cap checks.

### Fixed

- Fixed compatibility with Moonrise.

## [1.5.4]

### Added

- You can now use a dynamic setting multiple times in the config.
  For example: simulation distance from 10 to 5 → view distance from 12 to 8 → simulation distance from 5 to 2 → view
  distance from 8 to 4, etc.

### Fixed

- Fixed dynamic performance trying to change simulation distance below 5 on singleplayer. The client doesn't accept this
  and would put it back to 12 (default).
- Fixed villagers being unable to swim up in a 1x1 with lobotomization enabled.

## [1.5.3]

### Added

- New optimization for command block executions by caching parsed commands (disabled by default).
- New `enabled` field for dynamic settings which allows you to disable certain settings from being automatically
  changed.
  You can no longer remove the setting entirely as the config suggested doing before as this was broken.

### Fixed

- Fix dynamic settings not loading properly when the config file contained errors.
  This would cause things like the mobcap percentage to remain uninitialized at -1, stopping mobspawning.

## [1.5.2]

### Fixed

- Fixed compatibility with 1.20.6 NeoForge loader changes.
- Fixed a bug with activation range immunization that unnecessarily broke redstone contraptions.

## [1.5.1]

### Changed

- `mobcap-modifier` was replaced with `additional-capacity` in the mobspawning configuration.
  - Uses a constant value instead of a modifier as it isn't affected by changes in mobcap percentage.
  - Very low mobcap percentages could otherwise cause spawners and such to basically never spawn with some
    configurations.

## [1.5.0]

### Changed

- Entirely rewritten the
  mod's [configuration](<https://github.com/Wesley1808/ServerCore/tree/v1.5.0/docs/config/DEFAULT.md>) in YAML. Config
  files are now stored under `config/servercore`.
- Expanded upon activation range by making activation types fully configurable.
- Improved the `/statistics entities` command to show how many mobs are affected by activation range.
- Added a much requested mobspawning configuration:
  - Has the ability to modify the spawnrate and mobcaps of each individual spawngroup.
  - Provides optional functionality to force mobcaps to apply to zombie reinforcements, spawners and nether portal
    randomticks.

### Fixed

- Fixed several issues with activation range.
- Fixed an incompatibility with night-config-fixes (will need dependency override for now).
- Improved compatibility with some chunk loader mods regarding random ticking.
- Fixed the mod icon resource.