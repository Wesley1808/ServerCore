# ServerCore
A fabric mod that aims to optimize & add multiplayer features for the minecraft server.\
This includes some ports of PaperMC (and forks) patches.

**Warning!**\
Some changes might be detectable by very complex redstone machines.\
Some changes are still WIP and may be unstable in specific situations.

**Current features:**
- Per player mobspawning (port of PaperMC's implementation).
- Dynamic performance checks (changes settings depending on the mspt).
- Adjustable random-tick / mob-spawn distance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item merge radius & entity breeding limits.
- Option to slow down tick rates of villagers trapped in 1x1 spaces.
- Some optimizations and fixes for advancements, maps, entity navigation, chunk ticking and mob spawning.

**Planned features:**
- Entity Activation Range (Port from Spigot / PaperMC).

**Commands:**
- /setting <name> <value> - Allows for modifying config settings ingame.
- /info mobcaps | tps - Gives information about the current settings.

**Known incompatibilities:**
- /carpet lagFreeSpawning (from Carpet mod). -> If you want to use carpet, make sure this setting is set to false.

Most features are disabled by default and can be found in the config.
The config file can be found at `/config/servercore.json`
# Setup
1. Navigate to https://github.com/Wesley1808/ServerCore-Fabric/actions
2. Select the topmost workflow run.
3. Download "ServerCore" from below **Artifacts**.
4. Unzip the file and grab the .jar file **without** -dev or -sources.
