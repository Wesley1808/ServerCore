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
- /mobcaps - Displays the current per-player mobcap for the user.
- /sc tps - Gives information about the current settings.

Most features are disabled by default and can be found in the config.\
The config file can be found at `<server_dir>/config/servercore.toml`
```toml
# Lets you enable / disable certain features and modify them.
[features]
    # (Default = true) Stops the server from loading spawn chunks.
    disable_spawn_chunks = true
    # (Default = true) Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.
    use_distance_map = true
    # (Default = false) Allows xp orbs to merge with others that have different experience amounts.
    # This will also allow players to pickup xp much faster.
    fast_xp_merging = false
    # (Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.
    lobotomize_villagers = false
    # (Default = 20) Decides the interval in between villager ticks when lobotomized.
    lobotomized_tick_interval = 20
    # (Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.
    auto_save_interval = 5
    # (Default = 0.5) Decides the radius in blocks that items / xp will merge at.
    item_merge_radius = 0.5
    xp_merge_radius = 0.5

# Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.
[dynamic]
    # (Default = false) Enables this feature.
    enabled = false
    # (Default = 40) The average MSPT to target.
    target_mspt = 40
    # (Default = [Max: 10, Min: 2]) Distance in which random ticks and mobspawning can happen.
    max_chunk_tick_distance = 10
    min_chunk_tick_distance = 2
    # (Default = [Max: 10, Min: 2]) Distance in which the world will tick, similar to no-tick-vd.
    max_simulation_distance = 10
    min_simulation_distance = 2
    # (Default = [Max: 10, Min: 2]) Distance in which the world will render.
    max_view_distance = 10
    min_view_distance = 2
    # (Default = [Max: 1.0, Min: 0.3]) Global multiplier that decides the percentage of the mobcap to be used.
    max_mobcap = 1.0
    min_mobcap = 0.3

# Stops animals / villagers from breeding if there are too many of the same type nearby.
[entity_limits]
    # (Default = false) Enables this feature.
    enabled = false
    # (Default = [Villager: 24, Animals: 32]) Maximum count before stopping entities of the same type from breeding.
    villager_count = 24
    animal_count = 32
    # (Default = [Villager: 64, Animals: 64]) The range it will check for entities of the same type.
    villager_range = 64
    animal_range = 64

# Allows you to disable specific commands and modify the way some of them are formatted.
[commands]
    # Enables / disables the /mobcaps command.
    command_mobcaps = true
    # The title for the /mobcaps command.
    mobcap_title = "§3Per Player Mobcaps (§a%MODIFIER%§3)"
    # The content for the /mobcaps command. This is displayed for every existing spawngroup.
    mobcap_spawn_group = "§8- §3%NAME%: §a%CURRENT% §8/ §a%CAPACITY%"

# Stops entities from ticking if they are too far away.
[activation_range]
    # (Default = false) Enables this feature.
    enabled = false
    # (Default = false) Enables vertical range checks. By default, activation ranges only work horizontally.
    # This can greatly improve performance on taller worlds, but might break a few very specific ai-based mobfarms.
    use_vertical_range = false
    # (Default = false) Allows villagers to tick regardless of the activation range.
    villager_tick_always = false
    # (Default = true) Allows villagers to tick regardless of the activation range when panicking.
    villager_tick_panic = true
    # (Default = 20) The time in seconds that a villager needs to be inactive for before obtaining work immunity (if it has work tasks).
    villager_work_immunity_after = 20
    # (Default = 20) The amount of ticks an inactive villager will wake up for when it has work immunity.
    villager_work_immunity_for = 20
    # 
    # The settings below will only take effect after a restart!
    # Activation Range = The range an entity is required to be in from a player to tick.
    # Tick Inactive = if an entity is allowed to tick once per second whilst inactive.
    # Wakeup Interval = The interval between inactive entity wake ups in seconds.
    # 
    # Activation range settings for villagers.
    villager_activation_range = 16
    villager_tick_inactive = false
    villager_wakeup_interval = 30
    # Activation range settings for monsters.
    monster_activation_range = 32
    monster_tick_inactive = true
    monster_wakeup_interval = 20
    # Activation range settings for animals.
    animal_activation_range = 16
    animal_tick_inactive = false
    animal_wakeup_interval = 60
    # Activation range settings for neutral mobs.
    neutral_activation_range = 24
    neutral_tick_inactive = false
    neutral_wakeup_interval = 30
    # Activation range settings for water mobs.
    water_activation_range = 16
    water_tick_inactive = false
    water_wakeup_interval = 60
    # Activation range settings for zombies.
    zombie_activation_range = 16
    zombie_tick_inactive = true
    zombie_wakeup_interval = 60
    # Activation range settings for flying mobs.
    flying_activation_range = 32
    flying_tick_inactive = true
    flying_wakeup_interval = 10
    # Activation range settings for raider mobs.
    raider_activation_range = 48
    raider_tick_inactive = true
    raider_wakeup_interval = 20
    # Activation range settings for miscellaneous entities.
    misc_activation_range = 16
    misc_tick_inactive = false
    misc_wakeup_interval = 60
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