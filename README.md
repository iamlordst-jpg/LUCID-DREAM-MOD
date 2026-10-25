# Lucid Dream Simulator
**Minecraft Java Edition · Forge 1.20.1**

A surreal sandbox mod focused on lucid-dream-inspired reality control, dream exploration, and strange dream entities.

## Current implementation
- Forge 1.20.1 project scaffold with Java 17 and reproducible GitHub Actions build.
- 100-entry dream power catalogue with command-driven activations.
- Dream Journal guide and a curated entity/encounter design catalogue.
- Power command: `/dreampower list`, `/dreampower use <power_id>`.
- The current power layer uses Minecraft/Forge-native mechanics so the core mod does not require extra runtime libraries.

## Build
Run `./gradlew build` with JDK 17. The mod JAR is written to `build/libs/`.

## Recommended optional runtime mods
- **GeckoLib 4** — custom animated dream entities.
- **Patchouli** — in-game dream journal / guidebook.
- **Curios API** — wearable dream relic slots.
- **JEI** — recipe and item discovery.
These are planned integration targets, not mandatory dependencies of the current core source. Add a library only when the relevant integration is implemented.

## Power command
Use `/dreampower list` to print the catalogue and `/dreampower use <id>` to activate a power. Commands are available to operators in this initial foundation build.

## Entity design roster
See `docs/DREAM_ENTITIES.md` for the recommended encounter roster and behavior concepts.
