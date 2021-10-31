# ServerCore
A fabric mod that aims to optimize & add multiplayer features to the minecraft server.\
This includes some ports of PaperMC (and forks) patches.

**Warning!**\
Some changes might be detectable by very complex redstone machines.\
Some changes are still WIP and may be unstable in specific situations.

**Current features:**
- Per player mobspawning (port of PaperMC's implementation).
- Optional Entity Activation Range (Port of Spigot's & PaperMC's implementation).
- Dynamic performance checks (changes settings depending on the mspt).
- Adjustable chunk-ticking distance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item / xp merge radius & entity breeding limits.
- Option to slow down tick rates of villagers trapped in 1x1 spaces.
- Some optimizations and fixes for advancements, maps, entity navigation, chunk ticking and mob spawning.

**Commands:**
- /servercore <name> <value> - Allows for modifying settings ingame.
- /mobcaps - Displays current mobcaps, supports both per-player and global.
- /sc tps - Gives information about the current settings.

**Known incompatibilities:**
- /carpet lagFreeSpawning (from Carpet mod). -> If you want to use carpet, make sure this setting is set to false.

Most features are disabled by default and can be found in the config.\
The config file can be found at `<server_dir>/config/servercore.toml`
```toml
# Configuration for ServerCore - Fabric

[features] # Lets you enable / disable certain features and modify them.
    # Replaces vanilla's mobspawning algorithm with PaperMC's per player mobspawning.
    per_player_spawns = false
    # Stops the server from loading spawn chunks.
    disable_spawn_chunks = false
    # Allows xp orbs to merge with others that have different experience amounts.
    # This will also allow players to pickup xp much faster.
    fast_xp_merging = false
    # Allows ServerCore to modify the distance the server will perform chunk ticks at.
    # Chunk ticks include randomticks and mobspawning.
    enable_chunk_tick_distance = false
    # Makes villagers tick less often if they are stuck in a 1x1 space.
    # The tick interval decides the interval between villager ticks when lobotomized.
    lobotomize_villagers = false
    lobotomized_tick_interval = 20
    # The amount of minutes in between auto-save intervals when /save-on is active.
    autosave_interval_minutes = 5
    # Decides the radius in blocks that items / xp will merge at.
    item_merge_radius = 0.5
    xp_merge_radius = 0.5

[entity_limits] # Stops animals / villagers from breeding if there are too many of the same type nearby.
    enabled = false
    # Count = maximum count before stopping entities of the same type from breeding.
    villager_count = 24
    animal_count = 32
    # Range = the range it will check for entities of the same type.
    villager_range = 64
    animal_range = 64

[dynamic] # Modifies mobcaps, no-chunk-tick and view-distance depending on the MSPT.
    enabled = false
    # The maximum and minimum permitted values.
    # Chunk tick distance = distance in which random ticks and mobspawning can happen.
    max_chunk_tick_distance = 10
    min_chunk_tick_distance = 2
    # View distance = distance in which the world will render.
    max_view_distance = 10
    min_view_distance = 2
    # Mobcap = global multiplier that decides the percentage of the mobcap to be used.
    max_mobcap = 1.0
    min_mobcap = 0.3
```

# Setup
1. Navigate to https://github.com/Wesley1808/ServerCore-Fabric/actions
2. Select the topmost workflow run.
3. Download "ServerCore" from below **Artifacts**.
4. Unzip the file and grab the .jar file **without** -dev or -sources.

# License
ServerCore includes ports of patches from repositories such as PaperMC and Purpur.\
If a file uses the GPL-3.0 license it will be stated at the top of the file.\
All other files are licensed under MIT.