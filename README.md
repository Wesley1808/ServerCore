# ServerCore
A fabric mod that aims to optimize & add multiplayer features to the minecraft server.\
This includes some ports of PaperMC (and forks) patches.

**Warning!**\
Some changes might be detectable by very complex redstone machines.\
Some changes are still WIP and may be unstable in specific situations.

**Current features:**
- Dynamic performance checks (changes settings depending on the mspt).
- Adjustable chunk-ticking distance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item / xp merge radius & entity breeding limits.
- Option to slow down tick rates of villagers trapped in 1x1 spaces.
- Some optimizations and fixes for advancements, maps, entity navigation, chunk ticking and mob spawning.

**Planned features:**
- Entity Activation Range (Port from Spigot / PaperMC).

**Commands:**
- /servercore <name> <value> - Allows for modifying settings ingame.
- /mobcaps - Displays current global mobcaps.
- /sc tps - Gives information about the current settings.

**Known incompatibilities:**
- /carpet lagFreeSpawning (from Carpet mod). -> If you want to use carpet, make sure this setting is set to false.

Most features are disabled by default and can be found in the config.\
The config file can be found at `<server_dir>/config/servercore.toml`
```toml
# Configuration for ServerCore - Fabric

# Lets you enable / disable certain features and modify them.
[features]
  per_player_spawns = false
  disable_spawn_chunks = false
  fast_xp_merging = false
  enable_chunk_tick_distance = false
  lobotomize_villagers = false
  lobotomized_tick_interval = 20
  autosave_interval_minutes = 5
  xp_merge_radius = 0.5
  item_merge_radius = 0.5

# Stops animals / villagers from breeding if there are too many of the same type nearby.
[entity_limits]
  enabled = false
  villager_count = 24
  villager_range = 64
  animal_count = 32
  animal_range = 64

# Modifies mobcaps, no-chunk-tick and viewdistance depending on the MSPT.
[dynamic]
  enabled = false
  max_chunk_tick_distance = 10
  min_chunk_tick_distance = 2
  max_view_distance = 10
  min_view_distance = 2
  max_mobcap = 1.0
  min_mobcap = 0.3
```

# Setup
1. Navigate to https://github.com/Wesley1808/ServerCore-Fabric/actions
2. Select the topmost workflow run.
3. Download "ServerCore" from below **Artifacts**.
4. Unzip the file and grab the .jar file **without** -dev or -sources.
