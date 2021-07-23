# ServerCore Fabric
A fabric mod that adds certain features and tools for multiplayer servers to use.\
\
**Current features:**
- Per player mobspawning (similar to PaperMC's implementation).
- Dynamic performance checks (changes settings depending on the mspt).
- No-tick viewdistance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item merge radius & entity breeding limits.
- Some optimizations for the EntityTracker and spawn methods.

**Commands:**
- /setting <name> <value> - Allows for modifying config settings at runtime.
- /info mobcaps | tps - Gives information about the current settings.

The config file can be found at `/config/servertools.json`
