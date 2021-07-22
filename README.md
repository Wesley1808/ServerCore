# Fabric Server Tools
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
- /mobcaps - Displays the current mobcaps.
- /tps - Displays current TPS, MSPT, Online players, tick/view distances and the mobcap multiplier.

The config file can be found at `/config/servertools.json`
