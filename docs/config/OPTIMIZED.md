# Optimized Configuration

Below you can find an example of a more heavily optimized configuration for ServerCore.\
This configuration enables some more aggressive optimizations and features that can drastically improve performance,\
but can also deviate from vanilla behavior a bit.

This example config is designed for public servers that are willing to sacrifice some vanilla behavior for performance.\
You can of course always tweak these even further to your liking, but this should provide a good starting point.

### optimizations.yml

```yml
# Allows you to toggle specific optimizations that don't have full vanilla parity.
# These settings will only take effect after server restarts.

# Prevents many different lagspikes caused by loading chunks synchronously.
# This for example causes maps to only update loaded chunks, which depending on the viewdistance can be a smaller radius than vanilla.
reduce-sync-loads: true
# Can significantly reduce the time spent on chunk iteration by caching ticking chunks every second.
# This is especially useful for servers with a high playercount and / or viewdistance.
# Note: The list of ticking chunks is only updated every second, rather than every tick (but that is very unlikely to matter).
cache-ticking-chunks: true
# Can significantly reduce time spent on mobspawning, but isn't as accurate as vanilla on biome borders.
# This may cause mobs from another biome to spawn a few blocks across a biome border (this does not affect structure spawning!).
fast-biome-lookups: true
# Fluid random ticks, like lava spreading fire, are run twice each game tick.
# Enabling this will cancel the 'duplicate' second fluid tick, but this may cause slight behavior changes.
cancel-duplicate-fluid-ticks: true
```

### config.yml

```yml
# The main configuration file for ServerCore.
# Most of these settings can be reloaded without restarting using /servercore reload.

# Most miscellaneous feature toggles.
features:
  # Stops the server from loading spawn chunks.
  disable-spawn-chunks: true
  # Prevents lagspikes caused by players moving into unloaded chunks.
  prevent-moving-into-unloaded-chunks: true
  # The amount of seconds between auto-saves when /save-on is active.
  autosave-interval-seconds: 600
  # The fraction that decides the chance of experience orbs being able to merge with each other. (1 = 100%, 40 = 2.5%)
  # Note that just like in vanilla, experience orbs will still need to be of the same size to actually merge.
  xp-merge-fraction: 8
  # The radius in blocks that experience orbs will merge at.
  xp-merge-radius: 3.0
  # The radius in blocks that items will merge at.
  item-merge-radius: 2.0
  lobotomize-villagers:
    # Makes villagers tick less often if they are stuck in a 1x1 space.
    enabled: true
    # Decides the interval in between villager ticks when lobotomized.
    tick-interval: 20

# Automatically modifies dynamic settings based on the server performance.
dynamic:
  # Enables dynamic performance checks.
  enabled: true
  # The average MSPT to target.
  target-mspt: 35
  # The settings that will be decreased when the server is overloaded, in the specified order.
  # Removing a setting from the list will disable it.
  # ► max = The maximum value the server will increase the setting to.
  # ► min = The minimum value the server will decrease the setting to.
  # ► increment = The amount the setting will be increased or decreased by.
  # ► interval = The amount of seconds between each check to increase or decrease.
  dynamic-settings:
    - setting: 'CHUNK_TICK_DISTANCE'
      max: 8
      min: 2
      increment: 1
      interval: 15

    - setting: 'MOBCAP_PERCENTAGE'
      max: 100
      min: 30
      increment: 10
      interval: 15

    - setting: 'SIMULATION_DISTANCE'
      max: 8
      min: 2
      increment: 1
      interval: 15

    - setting: 'VIEW_DISTANCE'
      max: 10
      min: 4
      increment: 1
      interval: 300

# A special mobcap that only affects the breeding of animals and villagers.
breeding-cap:
  # Enables breeding caps.
  enabled: true
  # The breeding cap for villagers.
  # ► limit = The limit of mobs of the same type within range. Setting this to negative will disable the breeding cap.
  # ► range = The range it will check for entities of the same type.
  villagers:
    limit: 24
    range: 64

  # The breeding cap for animals.
  # Note that this cap only checks for animals of the same type.
  # If the limit is 32 you can still breed 32 cows and 32 pigs next to each other.
  animals:
    limit: 32
    range: 64

# Gives more control over mob spawning.
mob-spawning:
  # Mobcap settings for zombie reinforcements.
  # ► enforce-mobcaps = Whether to enforce mobcaps for this type of mobspawning.
  # ► mobcap-modifier = The modifier to apply to this enforced mobcap. This modifier only affects this type of mobspawning.
  # Since these mobspawns normally wouldn't be affected by the mobcap, you might want to allow them to spawn a bit over it.
  zombie-reinforcements:
    enforce-mobcap: true
    mobcap-modifier: 1.5

  # Mobcap settings for zombified piglin spawning from nether portal random ticks.
  nether-portal-randomticks:
    enforce-mobcap: true
    mobcap-modifier: 1.5

  # Mobcap settings for mobs spawned from monster spawners.
  monster-spawners:
    enforce-mobcap: true
    mobcap-modifier: 1.5

  # A list of mob categories with their respective mobcap and spawn interval.
  # ► category = The vanilla spawn category.
  # ► mobcap = The maximum amount of entities in the same category that can spawn near a player.
  # ► spawn-interval = The interval between spawn attempts in ticks. Higher values mean less frequent spawn attempts.
  categories:
    - category: 'MONSTER'
      mobcap: 70
      spawn-interval: 1

    - category: 'CREATURE'
      mobcap: 10
      spawn-interval: 400

    - category: 'AMBIENT'
      mobcap: 15
      spawn-interval: 1

    - category: 'AXOLOTLS'
      mobcap: 5
      spawn-interval: 1

    - category: 'UNDERGROUND_WATER_CREATURE'
      mobcap: 5
      spawn-interval: 1

    - category: 'WATER_CREATURE'
      mobcap: 5
      spawn-interval: 1

    - category: 'WATER_AMBIENT'
      mobcap: 20
      spawn-interval: 1

# Settings for commands and their formatting.
commands:
  # Enables the /servercore status command.
  status-enabled: true
  # Enables the /mobcaps command.
  mobcaps-enabled: true
  colors:
    # The colors used in command feedback. You can use hex codes or minecraft legacy color names.
    # The primary color is the most used color in command feedback.
    primary: 'dark_aqua'
    # The secondary color is used for highlighting important information, like values.
    secondary: 'green'
    # The tertiary color is mostly used for text in titles.
    tertiary: 'aqua'

# Activation range can drastically reduce the amount of lag caused by ticking entities.
# It does this by cleverly skipping certain entity ticks based on the distance to players and other factors, like immunity checks.
# Immunity checks determine whether an entity should be ticked even when it's outside the activation range, like for example when it is falling or takes damage.
# Note: while this is a very powerful feature, it can still slow down mobfarms and break very specific technical contraptions.
activation-range:
  # Enables activation range.
  enabled: true
  # Briefly ticks entities newly added to the world for 10 seconds (includes both spawning and loading).
  # This gives them a chance to properly immunize when they are spawned if they should be. Can be helpful for mobfarms.
  tick-new-entities: true
  # Enables vertical range checks. By default, activation ranges only work horizontally.
  # This can greatly improve performance on taller worlds, but might break a few very specific ai-based mobfarms.
  use-vertical-range: true
  # Skips 1/4th of entity ticks whilst not immune.
  # This affects entities that are within the activation range, but not immune (for example by falling or being in water).
  skip-non-immune: true
  # Allows villagers to tick regardless of the activation range when panicking.
  villager-tick-panic: true
  # The time in seconds that a villager needs to be inactive for before obtaining work immunity (if it has work tasks).
  villager-work-immunity-after: 20
  # The amount of ticks an inactive villager will wake up for when it has work immunity.
  villager-work-immunity-for: 20
  # A list of entity types that should be excluded from activation range checks.
  excluded-entity-types:
    - 'minecraft:warden'
    - 'minecraft:hopper_minecart'
    - 'minecraft:ghast'
  # The activation type that will get assigned to any entity that doesn't have a custom activation type.
  # ► activation-range = The range an entity is required to be in from a player to be activated.
  # ► tick-interval = The interval between 'active' ticks whilst the entity is inactive. Negative values will disable these active ticks.
  # ► wakeup-interval = The interval between inactive entity wakeups in seconds.
  # ► extra-height-up = Allows entities to be ticked when far above the player when vertical range is in use.
  # ► extra-height-down = Allows entities to be ticked when far below the player when vertical range is in use.
  default-activation-type:
    activation-range: 16
    tick-interval: 20
    wakeup-interval: -1
    extra-height-up: false
    extra-height-down: false

  # A list of custom activation types.
  # ► name = The name of the activation type.
  # ► entity-matcher = A list of conditions to filter entities. Only one of these conditions needs to be met for an entity to match.
  # ► If an entity matches multiple activation types, the one highest in the list will be used. The conditions accept the following formats:
  #   - Entity type matching    |   Uses the entity type's identifier.  |  'minecraft:zombie' matches zombies, but for example not husks or drowned.
  #   - Typeof class matching   |   Uses the 'typeof:' prefix.          |  'typeof:monster' matches all monsters.
  # ► Available typeof classes: mob, monster, raider, neutral, ambient, animal, water_animal, flying_animal, flying_monster, villager.
  custom-activation-types:
    - name: 'raider'
      activation-range: 48
      tick-interval: 20
      wakeup-interval: 20
      extra-height-up: true
      extra-height-down: false
      entity-matcher:
        - 'typeof:raider'

    - name: 'water'
      activation-range: 24
      tick-interval: 20
      wakeup-interval: 60
      extra-height-up: false
      extra-height-down: false
      entity-matcher:
        - 'typeof:water_animal'

    - name: 'villager'
      activation-range: 16
      tick-interval: 20
      wakeup-interval: 30
      extra-height-up: false
      extra-height-down: false
      entity-matcher:
        - 'typeof:villager'

    - name: 'zombie'
      activation-range: 16
      tick-interval: 20
      wakeup-interval: 20
      extra-height-up: true
      extra-height-down: false
      entity-matcher:
        - 'minecraft:zombie'
        - 'minecraft:husk'

    - name: 'monster-below'
      activation-range: 32
      tick-interval: 20
      wakeup-interval: 20
      extra-height-up: true
      extra-height-down: true
      entity-matcher:
        - 'minecraft:creeper'
        - 'minecraft:slime'
        - 'minecraft:magma_cube'
        - 'minecraft:hoglin'

    - name: 'flying-monster'
      activation-range: 48
      tick-interval: 20
      wakeup-interval: 20
      extra-height-up: true
      extra-height-down: false
      entity-matcher:
        - 'minecraft:ghast'
        - 'minecraft:phantom'

    - name: 'monster'
      activation-range: 32
      tick-interval: 20
      wakeup-interval: 20
      extra-height-up: true
      extra-height-down: false
      entity-matcher:
        - 'typeof:monster'

    - name: 'animal'
      activation-range: 16
      tick-interval: 20
      wakeup-interval: 60
      extra-height-up: false
      extra-height-down: false
      entity-matcher:
        - 'typeof:animal'
        - 'typeof:ambient'

    - name: 'creature'
      activation-range: 24
      tick-interval: 20
      wakeup-interval: 30
      extra-height-up: false
      extra-height-down: false
      entity-matcher:
        - 'typeof:mob'
```