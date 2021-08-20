# ServerCore Fabric
A fabric mod that adds certain features and tools for multiplayer servers to use.\
\
**Current features:**
- Per player mobspawning (port of PaperMC's implementation).
- Dynamic performance checks (changes settings depending on the mspt).
- Adjustable random-tick / mob-spawn distance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item merge radius & entity breeding limits.
- Option to slow down tick rates of villagers trapped in 1x1 spaces.
- Some optimizations and fixes for advancements, maps, entity navigation, chunk ticking and mob spawning.

**Commands:**
- /setting <name> <value> - Allows for modifying config settings at runtime.
- /info mobcaps | tps - Gives information about the current settings.

**Known incompatibilities:**
- /carpet lagFreeSpawning (from Carpet mod). -> If you want to use carpet, make sure this setting is set to false.

The config file can be found at `/config/servercore.json`
