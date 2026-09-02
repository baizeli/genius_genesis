# Protected Health Dummy Design

## Goal

Add a self-contained protected-health subsystem and a test zombie dummy with 800 maximum health. The subsystem must match Astralrail Cube's defensive model closely: reverse-counted authoritative health stored as an encrypted string, guarded access, custom alive/death decisions, and no Java agent. Direct modification of `LivingEntity.DATA_HEALTH_ID` must not change authoritative health, while ordinary combat damage must still defeat the entity.

Entity removal is explicitly outside the protection boundary. Calls such as `remove`, `discard`, and `setRemoved` remain effective.

## Package Boundary

All protected-health implementation belongs under:

`miku.united_as_one.genesis.combat.protectedhealth`

The package contains the cipher, access manager, Unsafe wrapper or adapter, protected entity base behavior, and the zombie dummy. Existing registry and client setup classes only receive the minimal registration hooks required for the new entity.

## Health Representation

Authoritative health is a reverse-counted value:

- Spawn value: `-800.0F`.
- Damage: add final accepted damage to the value.
- Healing: subtract healing, clamped to `-800.0F`.
- Alive: authoritative value is less than zero.
- Dead or dying: authoritative value is greater than or equal to zero.
- Remaining health: negate the authoritative value and clamp to `[0, 800]`.

The authoritative value is converted to a string, transformed by a local cipher modeled on Astralrail Cube's `SeraCipher`, and stored in a dedicated `SynchedEntityData<String>` entry. Neither the data accessor name nor entity NBT uses a semantic health key.

The actual health value is not written to entity NBT. Loading or re-adding the entity initializes authoritative health to full, matching the reference entity's behavior. Auxiliary state may be persisted only if it does not reveal or reconstruct current authoritative health.

## Access Protection

A manager modeled on Astralrail Cube's `MonsterManager` owns authoritative reads and writes. It uses:

- a runtime-generated hidden nestmate;
- cached `MethodHandle` entry points;
- `StackWalker` caller validation;
- package and class-loader validation;
- the project's cached `sun.misc.Unsafe` wrapper for low-level field/access operations where needed.

No `Instrumentation`, self-attach, embedded agent JAR, or runtime retransformation is used.

The protection is intended to resist ordinary API calls, reflection-based discovery, and direct vanilla health writes. It is not claimed to be absolute security against arbitrary code executing inside the same JVM.

## Vanilla Health Mirror

`LivingEntity.DATA_HEALTH_ID` is a non-authoritative mirror. It is always derived from authoritative remaining-health percentage:

`mirror = mirrorMaximum * remainingHealth / 800`

The synchronization direction is exclusively authoritative-to-mirror. A direct write to `DATA_HEALTH_ID` never heals, damages, or kills the dummy.

If authoritative health is 400 and an external caller writes mirror health to zero, the next server correction restores the mirror to 50 percent. After one additional point of accepted damage, authoritative health becomes 399 and the mirror becomes the corresponding 399/800 projection. It does not return to full health.

Mirror correction occurs during the protected entity's server tick and after accepted damage or healing. Client synchronization uses the normal dirty `SynchedEntityData` mechanism.

## Damage and Healing

The dummy overrides the damage path rather than allowing vanilla `setHealth` to become authoritative.

Damage behavior:

1. Reject damage when normal Forge/Minecraft rules say the hit is invalid or during the configured invulnerability interval.
2. Obtain the accepted/final damage consistently with the entity's chosen armor and resistance behavior.
3. Add the accepted damage to reverse authoritative health.
4. Update the vanilla mirror and hurt feedback.
5. When the authoritative value reaches zero, authorize and invoke the normal death sequence exactly once.

Direct `setHealth`, `DATA_HEALTH_ID` writes, `kill`, or generic vanilla death checks cannot make the entity die while authoritative health remains below zero.

Healing changes only authoritative reverse health and then regenerates the mirror. It cannot exceed 800 remaining health.

## Death Behavior

The entity remains alive while reverse health is below zero. At zero or above it transitions to the normal dying path, allowing standard death animation, death event handling, and removal. A small internal guard prevents duplicate death entry.

No removal defense is implemented. External removal can delete the entity regardless of authoritative health, as explicitly requested.

## Test Entity

Register `protected_zombie_dummy` as a monster entity that subclasses the vanilla zombie and uses the vanilla zombie renderer/model. Its protected maximum health is fixed at 800.

The dummy retains zombie-compatible hurt, sound, animation, and death behavior so ordinary weapons and spells can exercise the real combat path. AI behavior will be reduced enough for repeatable testing while preserving the zombie entity contract.

The entity receives a summon egg or an equivalent existing-project registration path so it can be spawned reliably in development and production tests.

## ASM and Mixin Scope

Because the protected target is owned by this project, no universal transformer or agent is required. The first phase uses explicit overrides and the project's existing Mixin/ModLauncher facilities only where vanilla final or inherited behavior cannot be safely redirected.

Any ASM transformer must target a fixed known class and must not scan or rewrite every `LivingEntity` subclass. Mixin and ASM must not both wrap the same method return path.

## Verification

Automated or deterministic development checks cover:

- spawn state is 800 remaining authoritative health;
- ordinary damage reduces authoritative and mirror health proportionally;
- cumulative 800 accepted damage enters normal death;
- direct `DATA_HEALTH_ID = 0` does not change authoritative health or alive state;
- a zeroed mirror is restored to the current authoritative percentage, not full health;
- direct mirror over-healing does not heal authoritative health;
- healing changes authoritative health and mirror together without exceeding 800;
- save/load does not expose current authoritative health in entity NBT and reinitializes full health;
- normal removal remains effective;
- dedicated server startup does not load client renderer classes.

Manual verification includes spawning the dummy, reducing it to approximately half health, forcing `DATA_HEALTH_ID` to zero, hitting it once, and confirming that the mirror returns to slightly below half rather than full.

## Non-Goals

- No universal force-health tool in this phase.
- No Java agent or self-attachment.
- No protection against entity removal or world lifecycle cleanup.
- No promise of security against arbitrary hostile bytecode in the same JVM.
- No plaintext or directly parseable authoritative health in entity NBT.
