# DEVIATIONS — Worldbeast Rework

Departures from the Worldbeast Rework Spec (`specs/WORLDBEAST_REWORK_SPEC.md`) recorded per §0.

## W1

1. **Player-placed blocks built before W1 are untracked.** Tracking via `BlockEvent.EntityPlaceEvent` begins at W1 installation. Pre-existing player builds receive no ×1.5 nutrition bonus and no player-block resistance gate. Acceptable: the beast was already eating these areas under the old system.

2. **Entity nutrition values defined but not consumed.** `NUTRITION_ANIMAL` (25), `NUTRITION_MONSTER` (8), `NUTRITION_VILLAGER` (60) are constants in `ConversionMap`. No system consumes them until W3 Maws wire entity feeding.

3. **Fog/gloom uses MASS-based shim, not HUNGER/ATTENTION moods.** `GloomTracker` maps MASS thresholds to fog ceiling values (replacing old phase ceilings). Full re-key to HUNGER (RESTLESS +20%, SATED clears) and ATTENTION moods deferred to W5 presentation pass.

## W2

4. **Sore eruption omits bloom buds.** §3.3 specifies "2 bloom BUDS (0.4 scale, closed)" in the eruption sequence. The bloom entity is W4. Eruption has a clearly-marked `W4_HOOK: spawnBloomBuds(level, center, 2)` where buds will be added.

5. **FIRST_* gating mechanism deferred.** W2 emits FIRST_* director rows and persists per-world boolean flags, but the director `gate` command to suppress/enable events (§7/§8) is W5. Events fire unconditionally in W2.

6. **Bell-cleanse pain deferred.** §6 lists bell cleanse as a pain event. No player-facing bell-cleanse mechanic exists yet (the bell only adds attention +10). Pain fires from purge charge detonation and the `cleanseArea` director command. Bell-cleanse path will be wired when the mechanic is built.

7. **Maw force-close pain deferred.** §6 lists this as a pain event. Maws are W3. A hook is documented in `OrderManager.issuePainResponse()`.

8. **Spire-destroyed pain deferred.** Spire destruction is Otherside dimension content and is not wired as a pain event in W2.

9. **Noise events tracked globally per chunk region.** §3.3 says "noiseEvents(10min)" — W2 tracks a decaying integer counter per chunk-region in `SoreManager`, separate from `BreachData.noiseCharge` (which only tracks noise near existing breaches).

10. **Sore budget cap and concurrent limit.** §3.3 says "small-budget front" — implemented as a `budgetFraction` field on BreachData (Sores use 0.25 of main budget, configurable). Concurrent active Sore cap defaults to 6 (configurable). These values are not spec-defined; they are tuning knobs.

11. **Purge charge pain deferred.** §6 lists purge charge detonation as a pain event. The purge charge item (`ModItems.PURGE_CHARGE`) has no custom use behavior yet — it's a plain `Item` with no detonation code. Pain will be wired when the purge charge use mechanic is built.
