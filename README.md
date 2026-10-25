# Lucid Dream Simulator
**Minecraft Java Edition · Forge 1.20.1**

A surreal sandbox mod focused on lucid-dream-inspired reality control, dream exploration, and strange dream entities.

## Included in this foundation
- Forge 1.20.1 / Java 17 project structure.
- A catalogue of 100 dream power IDs, accessible through `/dreampower list`.
- Command-driven power activations through `/dreampower use <power_id>`.
- Implemented starter effects for movement, teleportation, vision, healing, defense, weather/time, knockback pulses, and summoning vanilla stand-ins.
- A curated 20-entity design roster in `docs/DREAM_ENTITIES.md`.
- GitHub Actions build workflow that compiles the mod and uploads the JAR artifact.

The 100 IDs are registered in the command catalogue; the first implementation pass gives specialized mechanics to core powers and a safe visual/status response to the remaining IDs. The custom dream dimension and bespoke animated entity roster are the next content layer, not falsely represented as already finished.

## Build
Install JDK 17 and Gradle 8.8, then run `gradle build --no-daemon`. The JAR is written to `build/libs/`. GitHub Actions runs the same build on pushes and pull requests.

## Recommended dependencies
The core mod intentionally depends only on Forge. Add these runtime mods for the matching integration stages:
- **GeckoLib 4** — custom animated nightmare entities, guides, and dream creatures.
- **Patchouli** — in-game Dream Journal and tutorial book.
- **Curios API** — wearable dream relics and charm slots.
- **JEI** — discoverability for dream relic recipes.
- **Kotlin for Forge** — only if Kotlin-based modules are introduced; not needed for the current Java code.

Do not install these as Gradle compile dependencies until their APIs are actually integrated. This keeps the base mod build smaller and avoids unnecessary hard dependencies.
