# Changelog

## [1.5.1]

### Changed
- `mobcap-modifier` was replaced with `additional-capacity` in the mobspawning configuration.
  - Uses a constant value  instead of a modifier as it isn't affected by changes in mobcap percentage.
  - Very low mobcap percentages could otherwise cause spawners and such to basically never spawn with some configurations.

## [1.5.0]

### Changed
- Entirely rewritten the mod's [configuration](<https://github.com/Wesley1808/ServerCore/tree/v1.5.0/docs/config/DEFAULT.md>) in YAML. Config files are now stored under `config/servercore`.
- Expanded upon activation range by making activation types fully configurable.
- Improved the `/statistics entities` command to show how many mobs are affected by activation range.
- Added a much requested mobspawning configuration:
  - Has the ability to modify the spawnrate and mobcaps of each individual spawngroup.
  - Provides optional functionality to force mobcaps to apply to zombie reinforcements, spawners and nether portal randomticks.

### Fixed
- Fixed several issues with activation range.
- Fixed an incompatibility with night-config-fixes (will need dependency override for now).
- Improved compatibility with some chunk loader mods regarding random ticking.
- Fixed the mod icon resource.