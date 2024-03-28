# ServerCore

[
![Discord](https://img.shields.io/discord/998162243852173402?style=flat&label=Discord&logo=discord&color=7289DA)
](https://discord.gg/Y9nC7Peq4m)
[
![Build Status](https://github.com/Wesley1808/ServerCore/actions/workflows/gradle.yml/badge.svg)
](https://github.com/Wesley1808/ServerCore/actions/workflows/gradle.yml)
[
![Modrinth](https://img.shields.io/modrinth/dt/servercore?color=00AF5C&label=Modrinth&style=flat&logo=modrinth)
](https://modrinth.com/mod/servercore)
[
![Curseforge](http://cf.way2muchnoise.eu/full_550579_Downloads.svg)
](https://www.curseforge.com/minecraft/mc-mods/servercore)

A mod that aims to optimize the minecraft server.\
Works on both dedicated servers and singleplayer!

#### This includes:

- Several patches & optimizations to improve performance and reduce lagspikes, which shouldn't make any noticeable changes during gameplay.


- Several configurable features that can heavily reduce lag, but have a slight impact on gameplay depending on the configuration.

### Warning
>Some of the above optimizations use algorithms that may alter vanilla mechanics in the slightest.\
>These patches should all have an on/off switch in the config. If one does not, feel free to report it.

## Features

#### Optimizations

A lot of the optimizations in this mod are focused on getting rid of the majority of random lagspikes on servers.\
Other than that, it also includes plenty of optimizations for things like chunk ticking, mob spawning, item frames and
player logins.

___
#### Entity Activation Range

A very configurable feature that allows you to drastically cut down on the amount of entities that have to be processed
on the server.\
This is a port based off of Spigot's and PaperMC's implementation, but more configurable with additional (optional) features.

___

#### Dynamic performance checks

Allows the server to automatically adjust the current settings depending on the tick time and config.\
These include: Chunk-tick distance, View distance, Simulation distance & Mobcaps.

___

#### Villager lobotomization

Allows the server to cut down on villager lag from large trading halls, by making villagers stuck inside 1x1 spaces tick less often.

___

#### Breeding Caps

A feature that allows you to set a cap on the amount of mobs of the same type that can be bred within a certain radius.\
This can be useful to prevent players from breeding thousands of animals like chickens or cows in a small area.

___

#### Chunk ticking distance

A setting that allows you to reduce the distance at which chunks can tick (mob spawns & random ticks).
\
\
**- And more!**

## Commands, Placeholders and Configuration

### Commands

- /servercore settings | config - Allows for modifying settings & configs ingame.

- /servercore status - Gives information about the current dynamic settings.

- /mobcaps - Displays current per-player mobcaps for each spawn group.

- /statistics entities | block-entities - Displays performance related statistics.

___

### Placeholders

- `%servercore:view_distance%` - The current view distance.
- `%servercore:simulation_distance%` - The current simulation distance.
- `%servercore:chunk_tick_distance%` - The current chunk tick distance.
- `%servercore:mobcap_percentage%` - The global mobcap percentage that is currently being used.


- `%servercore:chunk_count%` - The amount of chunks on the server (doesn't have to be fully loaded).
- `%servercore:chunk_count loaded%` - The amount of fully loaded chunks on the server (expensive).


- `%servercore:entity_count%` - The amount of loaded entities on the server.
- `%servercore:entity_count nearby%` - The amount of loaded entities nearby.


- `%servercore:block_entity_count%` - The amount of ticking block entities on the server.
- `%servercore:block_entity_count nearby%` - The amount of ticking block entities nearby.

___

### Config

Since most features are disabled by default it can be helpful to know where you can edit them.\
ServerCore has two configuration files, both of which can be found in `./config/ServerCore/`
- `config.yml` - Contains all the feature toggles.
- `optimizations.yml` - Contains all toggleable optimizations.

___

## License

ServerCore contains several ports based on patches from repositories such as PaperMC, Purpur and Airplane.\
If a file uses the GPL-3.0 license it will be stated at the top. All other files are licensed under MIT.
