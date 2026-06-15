# ECHO DIMENSION — PHASE 1 BUILD SPEC (the world)

Goal of Phase 1: **make the Echo Dimension exist and look right, so it can be walked through and
filmed.** Mirror terrain, echo block palette + wood set, bright-blue water, custom sky, retuned fog,
the new portal, Echo Spirits roaming. Everything else in the master doc hangs off this.

Build in the lettered order below; each sub-step is independently testable. Workflow unchanged:
**verify against committed source, never trust self-report; test in survival.** Tag paths are
**singular** (`tags/block/`, `tags/item/`) — check every time.

> **Dimension ID decision (recommended):** keep the internal dimension id `otherside:the_otherside`
> so the existing **portal framework code + FogRenderer** keep working untouched — only the *content*
> is replaced. Set the **display name** to "The Echo Dimension" via lang. New biome/noise ids are
> `otherside:echo`. (Renaming the dim id is possible but forces rewiring the portal code + resets old
> portals — not worth it. Confirm if you disagree.)

Asset legend: **[HAVE]** texture already delivered · **[GEN]** assistant will generate next ·
**[CODE]** no texture needed.

---

## 1A — Block palette + the echo-wood set  *(fully asset-ready — start here)*
**Textures [HAVE]** in the delivered zip → `assets/otherside/textures/block/`:
`echo_log`, `echo_log_top`, `echo_planks`, `sculk_grass_top/side/bottom`, `sculk_dirt`,
`sculk_stone`, `sculk_gravel`, `sculk_leaves`.

Register these blocks (properties mirror the overworld counterpart; **sound = sculk-family**, non-flammable):
- `sculk_grass` (grass-block: 3-texture top/side/bottom, snowy-capable optional), `sculk_dirt`,
  `sculk_stone` (deepslate-tier hardness), `sculk_gravel`, `sculk_leaves` (leaves: non-occluding,
  decay optional-off for the mirror).
- **Echo wood set** (the new wood type), all from `echo_log`/`echo_planks`:
  `echo_log`, `stripped_echo_log`, `echo_wood`, `stripped_echo_wood`, `echo_planks`, `echo_stairs`,
  `echo_slab`, `echo_fence`, `echo_fence_gate`, `echo_door`, `echo_trapdoor`, `echo_button`,
  `echo_pressure_plate`, `echo_sign` + `echo_hanging_sign`. Non-flammable (echo material). Strippable
  log→stripped via axe. Standard wood tags.

Each block needs: blockstate + model(s) + item model + loot table + correct **mineable** tags
(`tags/block/mineable/axe` for wood, `/pickaxe` for stone/gravel-as-stone? no — gravel uses shovel;
`sculk_grass`/`sculk_dirt`/`sculk_gravel` → `mineable/shovel`, `sculk_stone` → `mineable/pickaxe`,
`sculk_leaves` → `mineable/hoe` + `minecraft:leaves`). Add all to a creative tab (`Echo`).

**Verify 1A:** in creative, every block places with the right texture, drops correctly, mines with the
right tool, and the wood set crafts (planks→stairs/slab/etc.). No purple/black missing textures.

---

## 1B — Echo-water (bright bioluminescent blue)  *(texture [GEN])*
A custom fluid for the dimension's water — flows like water but **bright bioluminescent blue**
(reference: image 1), **emissive (light level ~7)**.
- Implement as a NeoForge fluid (still + flowing) with `echo_water_still` + `echo_water_flow`
  animated textures **[GEN]** (recolor of the existing `echo_fluid` to bright blue).
- Fluid block emits light ~7; tint bright blue; otherwise water-like physics.
- Used as the dimension's `default_fluid` in 1C so all mirrored water is echo-water.

**Verify 1B:** place/flow echo-water in the overworld test — glows blue, flows like water.

---

## 1C — The dimension + mirror generation + echo biome  *(core step)*
**The mirror (free, via shared seed):** a dimension using **overworld noise settings** generates the
**same terrain shape** as the overworld for the same world seed. So we copy overworld gen and only
swap the blocks.

1. **Noise settings** `otherside:echo` = a **copy of `minecraft:overworld` noise settings**, changed:
   - `default_block` → `otherside:sculk_stone`
   - `default_fluid` → `otherside:echo_water` (the 1B fluid's source state)
   - `surface_rule` → rewrite so **every** overworld surface material maps to an echo variant (the
     complete funnel below — not just grass/dirt). The keep-list and fluid choices are deliberate.

   **COMPLETE terrain funnel (catch every natural block; vegetation handled by features, not here):**
   | Overworld | → Echo |
   |---|---|
   | grass block, mycelium, podzol, snow block (top layer) | `sculk_grass` |
   | dirt, coarse dirt, rooted dirt, clay, mud, snow/powder snow (sub-layer) | `sculk_dirt` |
   | stone, deepslate, granite/diorite/andesite, tuff, calcite, dripstone, sandstone(all), terracotta(all) | `sculk_stone` |
   | sand, red sand, gravel | `sculk_gravel` |
   | water | `echo_water` |
   | ice, packed ice, blue ice | `sculk_stone` (frozen lakes stay solid/walkable) |
   | lava | `echo_water` (single dimension fluid) |

   **KEEP (do NOT funnel):** `amethyst` (geodes/buds/clusters) — it's the corruption cure/ward, must
   appear as a resource; `bedrock` (floor). **Ores → `sculk_stone` (NO ores generate in the Echo
   Dimension** — it has its own materials; no vanilla mining progression here). *Ice/lava mappings
   above are one-line tunable.*
2. **Dimension** `the_otherside` generator → noise generator using `otherside:echo` settings + fixed
   biome source `otherside:echo`. (World seed is shared by default → shape mirrors the overworld.)
3. **Dimension type** (`the_otherside`): `fixed_time` set to a **dusk value** (no day/night — the sky
   is a timeless overcast), `has_skylight=false` or low `ambient_light ~0.1` (oppressive but
   traversable — tune), `has_ceiling=false`, no bedrock roof.
4. **Echo biome** `otherside:echo` (replaces `the_abyss`):
   - Effects: fog/sky/water colors for the new look (water bright blue, fog dusky-teal — final sky via
     1D renderer). Mood ambient loop kept.
   - **Features:** echo trees only — configured/placed features building trees from `echo_log` +
     `sculk_leaves`, scattered at overworld-forest-like density. **No vanilla vegetation** (grass,
     flowers, sugar cane, cactus, kelp, mushrooms, etc.) — simply not added to the biome, so it never
     generates. The *only* vegetation is what we add deliberately (echo trees now; echo flora later).
     (Terrain *shape* mirrors exactly; vegetation is echo-themed, not tree-for-tree identical.)
   - Vanilla ores/caves inherited from the noise settings are fine for now (echo-ore reskin is later
     polish). Vanilla structures: disable for Phase 1 (set structure set to empty) — the Core and echo
     structures come later.

**Verify 1C:** enter via portal → terrain shape recognizably matches the overworld (same hills/coasts),
ground is sculk_grass/dirt/stone, water is glowing blue, echo trees dot the forests. Standing in the
same overworld coordinates shows the same landform.

---

## 1D — Custom sky + fog  *(particles/colors [CODE], optional gradient [GEN])*
Reference: image 3 — **dusky purple-grey overcast, no sun/moon, drifting green spore-lights.**
- Client `DimensionSpecialEffects` for `the_otherside`: disable sun, moon, clouds; custom sky =
  flat dusky gradient (purple-grey). No celestial bodies. (A gradient sky texture is **[GEN]** if a
  flat color render isn't enough.)
- Ambient **floating green spore particles** drifting through the air around the player (reuse a
  warped-spore-like particle recolored green; spawn rate sparse, image-3 density).
- **Fog:** retune the existing `FogRenderer` `OTHERSIDE_DIM` branch — colors to sit under the new sky
  (dusky far, teal near); keep the limited-visibility submerged feel.

**Verify 1D:** sky reads as image 3 (dusky, spore-lights, no sun/moon); fog feels oppressive but you
can navigate.

---

## 1E — The new portal  *(framework kept; textures [GEN])*
**Keep all portal logic** (frame detection, ignition, re-ignition, guardian-state machine). Replace
only the **visual blocks**.
- **Portal field block** (replaces the old portal block visual): a translucent, **emissive** block
  rendering the **sculk-teal cloudy energy field that scrolls upward** (reference: image 2's look,
  recolored sculk-teal). Animated field texture **[GEN]** (vertical-scrolling teal cloud flipbook).
  Same collision/teleport behavior as the current portal block; it just looks new.
- **Portal frame block** (new): dark **chiseled echo-brick** (deepslate/sculk-brick with engraved
  motif, image 2's border). Texture **[GEN]**. Frame detection should accept this block (and/or keep
  current frame block too — decide).
- Ignition reagent/recipe: **keep current** for Phase 1 (re-theme later if wanted).

**Verify 1E:** build frame → ignite → up-scrolling teal cloud field appears, glowing; stepping in
teleports to the Echo Dimension; return works.

---

## 1F — Spawns + teardown
- **Echo Spirits roam:** add **Echo Soul** natural spawns to the `otherside:echo` biome (monster
  category; tune weight/pack-size so the world feels hunted but not swarmed). They already deter from
  light/echo-anchors (existing behavior).
- **Old drones** (`wug`/`ward`/`warb`): keep them spawning as lesser ambient fauna for now, OR cut —
  **minor decision**, default keep. (Retheme/replace with corrupted animals in a later phase.)
- **Teardown:** delete the old `the_abyss` biome JSON, the old abyss noise settings, and the
  warped-spore particle config (replaced by 1C/1D). Keep the portal framework + dimension id.

**Verify 1F (Phase 1 done):** spawn into the Echo Dimension cold — mirrored sculk terrain, glowing
blue water, dusky spore sky, oppressive fog, echo forests, the new portal behind you, and Echo Spirits
hunting. Walk it, film it.

---

## ASSETS THE ASSISTANT WILL GENERATE NEXT (so 1B/1D/1E aren't blocked)
1. `echo_water_still` + `echo_water_flow` — bright blue, emissive, animated.
2. Portal field — sculk-teal cloudy **upward-scrolling** animated texture (image-2 look, sculk colors).
3. Portal frame block — chiseled echo-brick (image-2 border).
4. Green spore sky particle (recolor) + optional dusky sky gradient.

**1A can begin immediately** — its textures are already delivered.
