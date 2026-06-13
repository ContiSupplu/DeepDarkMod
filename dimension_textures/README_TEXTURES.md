# Dimension Texture Batch — Install & Model Notes

## Files → assets/otherside/textures/block/ (item sprite → textures/item/)
extinguished_torch.png, extinguished_soul_torch.png — vanilla torch layout (2px stick x7-8). Wall-torch variants REUSE the same texture via the vanilla wall_torch model template; do not request separate wall textures.
extinguished_lantern.png, echo_lantern.png, echo_lantern_out.png — CUSTOM BOX-MODEL ATLASES (see geometry below); not vanilla-lantern layout.
echo_lantern_item.png → textures/item/ — flat inventory sprite for the Echo Lantern item (the atlas textures will look scrambled if used as item sprites).
echo_anchor_charged.png / echo_anchor_spent.png — cube_all per blockstate (charged|spent).
echo_fluid_still.png + .png.mcmeta — animated 32-frame strip; mcmeta must sit beside the png. Flowing variant deferred (DEVIATIONS.md) — v1 lakes may use the still texture or a fluid-look block per the review decision.

## Lantern box model (build this model JSON; all three lantern textures share the layout)
Atlas regions: SIDE face 6×7 at uv(0,0) — used for north/south/east/west. TOP 6×6 at uv(8,0). BOTTOM 6×6 at uv(8,7). KNOB 2×2 at uv(0,8).
Elements: body from [5,0,5] to [11,7,11] (6×7×6), knob from [7,7,9] to [9,9,9]→ use [7,7,7]-[9,9,9] (2×2×2) with KNOB uv on all faces.
Echo Lantern blockstates map LIT: full→echo_lantern.png (light 14), low→echo_lantern.png (light handled by state, texture unchanged — flicker is light-level only), out→echo_lantern_out.png (light 0). Extinguished vanilla lantern uses extinguished_lantern.png, light 0.

## SCOPE REDUCTION (apply to the implementation plan)
Campfires and candles need NO custom blocks or textures: vanilla has unlit states. The suppression handler places the vanilla block with lit=false and cancels ignition attempts (flint/fire charge) on them in this dimension. Delete ExtinguishedCampfireBlock and ExtinguishedCandleBlock from the plan; record in DEVIATIONS.md.

## Emissive note (same as block-texture batch)
Bright pixels do not self-illuminate; the blocks' light values carry the glow. Echo lantern lit state light 14 per spec.
