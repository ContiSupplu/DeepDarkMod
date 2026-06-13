# The Listening Bloom ("The Attention") — Model & Rig Guide (v4 FINAL)

## Files
listening_bloom.bbmodel — Blockbench Modded Entity, texture embedded. 75 cubes.
listening_bloom.png — 128×128 texture. Petal UNDERSIDES are textured lighter (visible when closed/tilted — do not repaint them dark).

## v4 changes (base + detail pass)
Roots rebuilt as 6 two-segment arcing tendrils (yaw→pitch→curl chains, irregular) + 3 bark nubs; stalk tapered 2-stage with kink offset; each outer petal gains a raised glowing MIDRIB ridge (child of the petal pitch bone — folds automatically, never animate separately); 8 small rim FILLER petals (petalF_*_yaw→_pitch) densify the dish. Roots and nubs are STATIC — do not animate them during tracking; only `head` moves.

## v3 design: the petals ARE the dish
8 wide overlapping outer petals + 6 inner petals form the parabolic bowl themselves (no rigid tier plates). The glowing membrane + feed column + 3 feeler prongs sit at the dish FOCUS — an antenna feed — fully concealed when closed. Consequence: the model reads correctly in BOTH states: open = flower-antenna with luminous focus; closed = solid tapered bud with a faint glowing seam at the tip.

## Rig map (nested single-axis bones; baked rest = OPEN with asymmetry jitter)
- `head` (pivot 0,6,0) — tracking. Yaw free 360°; pitch CLAMP ±65° (beyond that the underside/roots break the silhouette — verified in preview). Lerp 0.12/tick toward target; this tilt-to-face IS the signature behavior.
- `petalO_*_yaw` (fixed) → `_pitch` → `_curl1` → `_curl2` ; `petalI_*_yaw` → `_pitch` → `_curl` ; `feeler_*_yaw` → `_pitch`.
- `stalk`, `root_*` — static (subtle stalk sway only).
ANIMATE RELATIVE TO BAKED VALUES (per-petal jitter is intentional; zeroing bones sterilizes the model).

## State poses (ABSOLUTE target values; verified in 3-state preview)
- OPEN (baked rest): outerO pitch ≈+22, curl1 ≈−12, curl2 ≈−18; petalI pitch ≈+40, curl ≈−15; feelers ≈+30.
- CLOSED (bud): petalO pitch +72, curl1 +20, curl2 +25; petalF pitch +76; petalI pitch +80, curl +15; feelers +85.
- Unfurl CLOSED→OPEN over ~40t, petals staggered 2t (ripple). Fold = reverse + one shriek. NEVER pop.
- Twitch (state E): 2–3 petals nearest the heard event, yaw ±8°, 3t attack/8t settle, one dry click.
- Sweep (state F): head yaw sine ±50°/~6s, petal pitch breathing ±4.

## Implementation (unchanged)
Entity: invulnerable, AI-less, immobile, hitbox 1.2×1.2. Spawns on sculk, manifests via unfurl. Glow: emissive or light-block follower, config. Scales 1.0 / 0.4 (buds). The Vast One is a separate asset.
