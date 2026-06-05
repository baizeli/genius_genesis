# Genius' Genesis Rewrite Blueprint

This document is the working blueprint for the full rewrite of Genius' Genesis.
The rewrite is not a cleanup pass over the old project. It is a new mod workspace
that keeps the useful design intent while rebuilding code, packages, registries,
resources, and generated data from a clean base.

## Goals

- Build a clean Forge 1.20.1 mod under `genius_genesis`.
- Keep the main class named `Genesis`.
- Keep the base package `miku.united_as_one.genesis`.
- Use ISS-style domain packages.
- Use L2Hostility-style direct mod code: centralized registries, compact event
  handlers, and behavior living close to the content that owns it.
- Keep registry classes reasonably large when that is clearer.
- Avoid service/manager abstraction layers unless they remove real duplication.
- Treat integrations as optional compat unless they are hard requirements.

## Non-Goals

- Do not preserve old `genesis_magic` save compatibility.
- Do not refactor or migrate `genesis_core` in this rewrite plan.
- Do not migrate `EntityUtil.java` automatically.
- Do not split registry files only to make them smaller.
- Do not add Patchouli, JEI, Curios, or Iron's Spellbooks as metadata
  dependencies unless the mod cannot load without them.
- Do not copy old code wholesale. Port behavior intentionally.

## Package Shape

```text
miku.united_as_one.genesis
  Genesis.java
  api/
  block/
  compat/
  config/
  damage/
  data/
  datagen/
  effect/
  entity/
  fluid/
  gui/
  handlers/
  item/
  mixin/
  network/
  particle/
  recipe_types/
  registries/
  render/
  setup/
  spell/
  util/
  worldgen/
```

The package layout follows mod-domain boundaries, not enterprise layering.
If a feature is naturally small, keep it in one class. Split only when the split
matches Minecraft concepts or repeated local patterns.

## Registry Style

Registry files stay centralized:

```text
registries/ItemRegistry.java
registries/BlockRegistry.java
registries/CreativeTabRegistry.java
registries/EntityRegistry.java
registries/EffectRegistry.java
registries/SoundRegistry.java
registries/ParticleRegistry.java
registries/MenuRegistry.java
registries/BlockEntityRegistry.java
registries/RecipeRegistry.java
```

Large registry files may use static initializer sections and small helper
methods. Do not split `ItemRegistry` into many tiny registries unless the file
becomes actively painful to edit.

## Event Style

Event handlers stay compact and direct:

```text
handlers/CommonEventHandler.java
handlers/LivingEventHandler.java
handlers/PlayerEventHandler.java
handlers/CombatEventHandler.java
handlers/ClientEventHandler.java
handlers/ModBusEventHandler.java
```

Event methods may contain simple behavior directly. Complex item, spell, or
entity behavior should move to the owning item/spell/entity class.

## Compat Rule

Optional integrations live under `compat/<modid>/`.
They are guarded with loader checks or optional event registration.
Metadata dependency entries are only added for hard requirements.

Current hard dependency:

- `genesis_api`

`D:\项目\Genesis Lib` is the source project for `genesis_api`. Prefer reading
that source before reimplementing tooltip particles, equipment stats, item
shader effects, damage policy helpers, or render helpers.

Current optional integrations expected later:

- Iron's Spellbooks
- Curios
- JEI
- Patchouli
- L2Tabs

## Migration Order

1. Build foundation
   - Gradle, dependencies, jar-in-jar, metadata, main class, empty registries.

2. Establish mod skeleton
   - Base packages, registry classes, setup handlers, creative tab, first
     harmless item.

3. Move simple content
   - Materials, basic items, basic blocks, tabs, tags, generated models.

4. Move equipment
   - Weapons, armor, curios-like items, attribute behavior, tooltips.

5. Move spell content
   - Spell schools, spell items, projectiles, effects, spell configs.

6. Move workbench systems
   - Recipe types, menus, block entities, screens, datagen.

7. Move entities and bosses
   - Entities first, AI second, render/model/animation last.

8. Move client visuals
   - Renderers, particles, shaders, item effects, overlays.

9. Move worldgen and data
   - Worldgen JSON/data providers, loot, recipes, tags, language.

10. Add optional compat
   - Add integrations only after base behavior works without them.

## Verification Gates

Every migration step should pass at least:

```text
gradlew classes
gradlew jarJar
```

When content or data generation changes:

```text
gradlew runData
```

Before larger milestones:

```text
gradlew build
```

## Current Decision Log

- Mod name: `Genius' Genesis`
- Mod id: `genius_genesis`
- Main class: `Genesis`
- Java base package: `miku.united_as_one.genesis`
- Old save compatibility: not required
- `genesis_core`: out of scope
- `EntityUtil.java`: out of scope unless requested
- Patchouli: optional compat, not a base dependency
