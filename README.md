# ServerCore

A fabric mod that aims to optimize the minecraft server.

#### This includes:

- Several patches & optimizations to improve performance and reduce lagspikes, which shouldn't make any noticeable changes during gameplay.


- Several configurable features that can heavily reduce lag, but have a slight impact on gameplay depending on the configuration.

### Warning
>Some of the above optimizations use algorithms that may alter vanilla mechanics in the slightest.\
>These patches should all have an on/off switch in the config. If one does not, feel free to report it.

## Features

#### Optimizations

Most optimizations in this mod are focused on getting rid of the majority of random lagspikes on servers.\
Besides that, it also includes optimizations for chunk ticking, mob spawning, maps and player logins.

___
#### Entity Activation Range

A very configurable and optional feature that allows you to drastically cut down on the amount of entities that have to
be processed on the server.\
This is a port based off of Spigot's and PaperMC's implementation, with extra features.

___

#### Chunk ticking distance

A setting that allows you to reduce the distance at which chunks can tick (mob spawns & random ticks).\
This allows for higher viewdistances at lower costs.

___

#### Dynamic performance checks

Allows the server to automatically adjust the current settings depending on the MSPT and config.\
These include: Chunk-tick distance, View distance, Simulation distance & mobcaps.

___

#### Villager lobotomization

Allows the server to cut down on villager lag from large trading halls, by slowing down the tick rate of villagers stuck
in 1x1 spaces.
\
\
**- And more!**

## Commands & Configuration

### Commands

- /servercore settings | config - Allows for modifying settings & configs ingame.

- /servercore status - Gives information about the current dynamic settings.

- /mobcaps - Displays current mobcaps for each spawn group.

- /statistics entities | block-entities - Displays performance related statistics.
___
### Config
Most features are disabled by default and can be found in the config.\
The config file can be found at `/config/servercore.toml`
```toml
# Lets you enable / disable certain features and modify them.
[features]
	# (Default = true) Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.
	use_distance_map = true
	# (Default = false) Stops the server from loading spawn chunks.
	disable_spawn_chunks = false
	# (Default = false) Allows more xp orbs to merge with one another, to reduce xp orb lag.
	fast_xp_merging = false
	# (Default = false) Prevents players from moving into unloaded chunks.
	prevent_moving_into_unloaded_chunks = false
	# (Default = 128) The radius at which the game will look for other portals. Lower values can reduce lagspikes.
	# Does not work if lithium is installed.
	portal_search_radius = 128
	# (Default = 16) The radius at which the game will try to create new portals.
	# It is recommended to make this smaller than the search radius.
	portal_create_radius = 16
	# (Default = true) Whether the custom portal radius will use the vanilla dimension scale.
	portal_search_vanilla_scaling = true
	# (Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.
	lobotomize_villagers = false
	# (Default = 20) Decides the interval in between villager ticks when lobotomized.
	lobotomized_tick_interval = 20
	# (Default = -1) The threshold MSPT that the server is allowed to run mid-tick chunk saves at.
	# Setting this value to negative will disable this threshold.
	chunk_save_threshold = -1
	# (Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.
	auto_save_interval = 5
	# (Default = 0.5) Decides the radius in blocks that items / xp will merge at.
	item_merge_radius = 0.5
	xp_merge_radius = 0.5

# Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.
[dynamic]
	# (Default = false) Enables this feature.
	enabled = false
	# (Default = 35) The average MSPT to target.
	target_mspt = 35
	# (Default = 15) The amount of seconds between dynamic performance updates.
	update_rate = 15
	# (Default = 150) The amount of seconds between dynamic viewdistance updates.
	# This value is separate from the other checks because it makes all clients reload their chunks.
	view_distance_update_rate = 150
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
	# Enables / disables the /servercore status command.
	command_status = true
	# Enables / disables the /mobcaps command.
	# Forcefully set to false by: VMP
	command_mobcaps = true
	# The title for the /mobcaps command.
	mobcap_title = "<dark_aqua>%LINE% <aqua>Mobcaps</aqua> (<aqua>%MODIFIER%</aqua>) %LINE%</dark_aqua>"
	# The content for the /mobcaps command. This is displayed for every existing spawngroup.
	mobcap_content = "<dark_gray>» <dark_aqua>%NAME%:</dark_aqua> <green>%CURRENT%</green> / <green>%CAPACITY%</green></dark_gray>"
	# The title for the /servercore status command.
	status_title = "<dark_aqua>%LINE% <aqua>ServerCore</aqua> %LINE%</dark_aqua>"
	# The content for the /servercore status command.
	status_content = "<dark_gray>» <dark_aqua>Version:</dark_aqua> <green>%VERSION%</green>\n» <dark_aqua>Chunk-Tick Distance:</dark_aqua> <green>%CHUNK_TICK_DISTANCE%</green>\n» <dark_aqua>Simulation Distance:</dark_aqua> <green>%SIMULATION_DISTANCE%</green>\n» <dark_aqua>View Distance:</dark_aqua> <green>%VIEW_DISTANCE%</green>\n» <dark_aqua>Mobcap Multiplier:</dark_aqua> <green>%MOBCAPS%</green></dark_gray>"
	# The title for the /statistics command.
	stats_title = "<dark_aqua>%LINE% <aqua>Statistics</aqua> %LINE%</dark_aqua>"
	# The content for the /statistics command.
	stats_content = "<dark_gray>» <dark_aqua>TPS:</dark_aqua> <green>%TPS%</green> - <dark_aqua>MSPT:</dark_aqua> <green>%MSPT%</green>\n» <dark_aqua>Total chunk count:</dark_aqua> <green>%CHUNK_COUNT%</green>\n» <dark_aqua>Total entity count:</dark_aqua> <green>%ENTITY_COUNT%</green>\n» <dark_aqua>Total block entity count:</dark_aqua> <green>%BLOCK_ENTITY_COUNT%</green></dark_gray>"
	# The title for the /statistics (block) entities command.
	stats_page_title = "<dark_aqua>%LINE% <aqua>%TITLE%</aqua> by <aqua>%TYPE%</aqua> %LINE%</dark_aqua>"
	stats_page_title_player = "<dark_aqua>%LINE% <aqua>%TITLE%</aqua> for <aqua>%PLAYER%</aqua> %LINE%</dark_aqua>"
	# The content for the /statistics (block) entities command. This is displayed for every entry.
	stats_page_content = "<green>%INDEX%. <dark_aqua>%NAME%</dark_aqua> %COUNT%</green>"
	# The footer for the /statistics (block) entities command.
	stats_page_footer = "<dark_aqua>%LINE% <green>%PREV_PAGE%</green> Page <aqua>%PAGE%</aqua> of <aqua>%PAGE_COUNT%</aqua> <green>%NEXT_PAGE%</green> %LINE%"

# Stops entities from ticking if they are too far away.
[activation_range]
	# (Default = false) Enables this feature.
	enabled = false
	# (Default = false) Enables vertical range checks. By default, activation ranges only work horizontally.
	# This can greatly improve performance on taller worlds, but might break a few very specific ai-based mobfarms.
	use_vertical_range = false
	# (Default = true) Allows villagers to tick regardless of the activation range when panicking.
	villager_tick_panic = true
	# (Default = false) Allows villagers to tick regardless of the activation range.
	villager_tick_always = false
	# (Default = 20) The time in seconds that a villager needs to be inactive for before obtaining work immunity (if it has work tasks).
	villager_work_immunity_after = 20
	# (Default = 20) The amount of ticks an inactive villager will wake up for when it has work immunity.
	villager_work_immunity_for = 20
	# Activation Range = The range an entity is required to be in from a player to tick.
	# Tick Inactive = Whether an entity is allowed to tick once per second whilst inactive.
	# Wakeup Max = The maximum amount of entities in the same group and world that are allowed to be awakened at the same time.
	# Wakeup Interval = The interval between inactive entity wake ups in seconds.
	# Activation range settings for villagers.
	villager_activation_range = 16
	villager_tick_inactive = true
	villager_wakeup_max = 4
	villager_wakeup_interval = 30
	# Activation range settings for monsters.
	monster_activation_range = 32
	monster_tick_inactive = true
	monster_wakeup_max = 8
	monster_wakeup_interval = 20
	# Activation range settings for animals.
	animal_activation_range = 16
	animal_tick_inactive = true
	animal_wakeup_max = 4
	animal_wakeup_interval = 60
	# Activation range settings for flying mobs.
	flying_activation_range = 48
	flying_wakeup_max = 8
	flying_wakeup_interval = 10
	flying_tick_inactive = true
	# Activation range settings for water mobs.
	water_activation_range = 16
	water_tick_inactive = true
	# Activation range settings for neutral mobs.
	neutral_activation_range = 24
	neutral_tick_inactive = true
	# Activation range settings for zombies.
	zombie_activation_range = 16
	zombie_tick_inactive = true
	# Activation range settings for raider mobs.
	raider_activation_range = 48
	raider_tick_inactive = true
	# Activation range settings for miscellaneous entities.
	misc_activation_range = 16
	misc_tick_inactive = true
```
## License

ServerCore includes ports of patches from repositories such as PaperMC and Purpur.\
If a file uses the GPL-3.0 license it will be stated at the top. All other files are licensed under MIT.