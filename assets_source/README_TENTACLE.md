# Maw Tentacle — Model, Rig & Animation Guide

## Files
maw_tentacle.bbmodel — Blockbench Modded Entity, texture + 6 animations embedded. 28 cubes, 119 units (~7.4 blocks) risen.
maw_tentacle.png — 256×256 abyss-skin texture (near-black, ring striations, dim veins densifying tipward, pale sucker discs, glowing hook tips).

## Rig
Pure nested chain: `seg0` (base) → `seg1` → … → `seg8` → `tip` (3 hooks + glow core). Baked rest = risen 3D coil (planar S-curve + helical y-twist so it reads curved from EVERY camera angle). Sucker nubs are children of their segments. `collar_*` = static torn-ground ring at the base.
ANIMATE ADDITIVELY over baked rotations (same rule as the bloom). For procedural sway in-game: each segment copies its parent's rotation 2–3 ticks late — lag down the chain IS the whip; no math beyond that.

## Embedded animations (Animate tab → press play)
- **emerge** (3.4s) — uncoils from a ground fold, base-first… ALL segments FREEZE mid-rise for 0.5s (it's deciding)… then a fast overshooting finish. Pair with a ground-burst particle screen at t=0 (debris hides the first frames — standard creature-feature trick) + deep rumble.
- **idle_sway** (10.5s loop) — heavy lagged weight, amplitude growing tipward; a 1.8s dead hold mid-loop broken by a zero-ease micro-adjust on seg6; tip drifts a slow figure-8.
- **strike** (1.05s) — windup, 0.12s frozen anticipation, whip wave traveling base→tip (62° at the tip), ringing decay.
- **grab** (2.8s) — short whip, then seg5–8 WRAP sequentially (25/38/52/66°), clench with strain tremor, base drags downward (pulling prey toward the throat).
- **retract** (1.5s) — tip folds first, accelerating collapse, final yank under at 1.32s. Despawn the entity at animation end, never before.
- **wounded_recoil** (1.2s) — violent alternating flinch, 3 decaying shudders, then a held hurt droop. Plays on bell pulse / amethyst resistance.

## Usage constraints (CANON — from design discussion)
Tentacles are VERBS, not scenery: they exist only for (1) Maw grasp duty and (2) the hunting-tendril finisher. Always rooted in sculk; emerge→act→retract; NEVER free-roaming, NEVER killable (damage → wounded_recoil, repeated damage → retract; no death, no drops, no kill credit).

## Entity implementation essentials
- Invulnerable=false BUT all damage routes to recoil logic, never health depletion. No AI goals; server state machine: EMERGING→IDLE→STRIKING→GRABBING→DRAGGING→RETRACTING (+RECOILING interrupt).
- Hitbox: tall thin column (1.4×7.5); fine for an unkillable entity. Grab detection = distance check from tip position (approximate tip at 6.5 blocks along the curve), not hitbox collision.
- GRAB mechanic: on grab connect — set target's position each tick toward the tentacle (riding-style or forced motion), escape by: dealing 8+ damage to the tentacle (recoil releases), an ally bell pulse, or holding an amethyst cluster BEFORE the grab (immune-anchor, it can't hold you). Drag speed 0.15 blocks/tick toward the maw throat. Director: /otherside tentacle release|spawn|retract.
- Sounds: placeholder = reuse bloom set pitched down 0.6 (fold=shriek on strike, alert boom on emerge, tremor on clench) until a dedicated tentacle set is synthesized. Particle beats: ground burst on emerge (block-crack particles of the ground block + 12 sculk souls), whip woosh trail (CRIT-style custom), clench = SCULK_CHARGE at wrap points.
- Scale variants: 1.0 standard Maw arm; 0.6 hunting-tendril finisher; up to 1.6 at the Breach (cap — beyond that cubes read).
- Glow: hook tips + tip core want emissive; in darkness the tip glow is the ONLY visible part at distance — three faint lights rising from the ground IS the emerge dread shot.


## v2 UPDATE — round-faking & the underground question
**Octagonal segments**: every segment contains a 45°-rotated twin cube (`seg*_oct` bones, children of the segment — animations move them automatically). The 8-point cross-section reads cylindrical. **Membrane fins**: zero-thickness plates on segs 1/2/3/5 with RAGGED ALPHA-CUTOUT edges + pinholes — torn organic silhouettes cubes can't make. Entity rendering alpha-tests by default; no render-type work needed.

**How the underground part stays hidden (answer to the Blockbench question):**
1. `emerge`/`retract` now keyframe the POSITION channel on seg0: the chain starts 126 units BELOW the entity origin and slides up. In Blockbench you still see it (no ground exists there) — scrub the timeline and read y-position, that part below 0 is underground in-game.
2. In-game, terrain occludes it automatically (depth rendering hides geometry behind solid blocks). NO clipping code needed.
3. Code obligations: renderer `shouldRender` must use an EXPANDED culling box (the model extends ±8 blocks from origin; default culling will pop it off-screen) — set noCulling or override the bounding box. Spawn placement prefers locally flat ground (on steep slopes the buried chain can poke out of a hillside; the placement check is cheaper than the embarrassment).
4. EXPORT CAVEAT: when converting position keyframes to vanilla AnimationDefinition, verify the unit scale on first run — if the tentacle rises 0.5 blocks instead of 8, the exporter divided by 16; multiply position values accordingly. This is the most common position-channel porting bug.
