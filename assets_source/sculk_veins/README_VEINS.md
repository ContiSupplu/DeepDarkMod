# Sculk Vein Cords — Block Spec, Models & Pulse System (NeoForge 1.21.1)

The Worldbeast's surface nervous system: sub-half-block cords (3px tall, 6px wide) that
auto-connect like redstone dust into continuous lines, and carry visible traveling PULSES.

## Textures → assets/otherside/textures/block/
- sculk_vein_cord.png — straight cord segment (band runs north-south)
- sculk_vein_node.png — junction/center blob
- sculk_vein_cord_charged.png + .mcmeta — 8-frame traveling pulse (frametime 2)
- sculk_vein_node_charged.png + .mcmeta — 8-frame throb

## Block: otherside:sculk_vein_cord
Properties: NORTH, SOUTH, EAST, WEST (bool) + CHARGED (bool). 10 blockstates per axis combo.

### Blockstate (multipart) → blockstates/sculk_vein_cord.json
```json
{ "multipart": [
  {"when":{"charged":"false"},"apply":{"model":"otherside:block/sculk_vein_node"}},
  {"when":{"charged":"true"},"apply":{"model":"otherside:block/sculk_vein_node_charged"}},
  {"when":{"charged":"false","north":"true"},"apply":{"model":"otherside:block/sculk_vein_arm"}},
  {"when":{"charged":"false","east":"true"},"apply":{"model":"otherside:block/sculk_vein_arm","y":90}},
  {"when":{"charged":"false","south":"true"},"apply":{"model":"otherside:block/sculk_vein_arm","y":180}},
  {"when":{"charged":"false","west":"true"},"apply":{"model":"otherside:block/sculk_vein_arm","y":270}},
  {"when":{"charged":"true","north":"true"},"apply":{"model":"otherside:block/sculk_vein_arm_charged"}},
  {"when":{"charged":"true","east":"true"},"apply":{"model":"otherside:block/sculk_vein_arm_charged","y":90}},
  {"when":{"charged":"true","south":"true"},"apply":{"model":"otherside:block/sculk_vein_arm_charged","y":180}},
  {"when":{"charged":"true","west":"true"},"apply":{"model":"otherside:block/sculk_vein_arm_charged","y":270}}
]}
```

### Model: models/block/sculk_vein_arm.json (charged variant = same geometry, charged texture)
```json
{ "render_type": "minecraft:cutout",
  "textures": {"v": "otherside:block/sculk_vein_cord", "particle": "otherside:block/sculk_vein_cord"},
  "elements": [{
    "from": [5, 0, 0], "to": [11, 3, 5],
    "faces": {
      "up":    {"uv": [5, 0, 11, 5],  "texture": "#v"},
      "down":  {"uv": [5, 11, 11, 16],"texture": "#v"},
      "north": {"uv": [5, 0, 11, 3],  "texture": "#v"},
      "east":  {"uv": [4, 0, 9, 3],   "texture": "#v"},
      "west":  {"uv": [7, 0, 12, 3],  "texture": "#v"}
    }
  }]
}
```
(No south face — it tucks under the node. Verify side UVs visually in Blockbench; the band sits in columns 4-12.)

### Model: models/block/sculk_vein_node.json
```json
{ "render_type": "minecraft:cutout",
  "textures": {"v": "otherside:block/sculk_vein_node", "particle": "otherside:block/sculk_vein_node"},
  "elements": [{
    "from": [4, 0, 4], "to": [12, 3, 12],
    "faces": {
      "up":   {"uv": [4, 4, 12, 12], "texture": "#v"},
      "down": {"uv": [4, 4, 12, 12], "texture": "#v"},
      "north":{"uv": [4, 6, 12, 9],  "texture": "#v"},
      "south":{"uv": [4, 6, 12, 9],  "texture": "#v"},
      "east": {"uv": [4, 6, 12, 9],  "texture": "#v"},
      "west": {"uv": [4, 6, 12, 9],  "texture": "#v"}
    }
  }]
}
```

## Behavior
- **Survival**: like carpet — requires sturdy UP face below; pops on support loss (no drop, squelch sound + 2 sculk particles).
- **Connections** (`getStateForPlacement` + `updateShape`): set a direction true when the neighbor is another vein cord OR in tag `#otherside:vein_connectable` → tags/block/vein_connectable.json: sculk, sculk_catalyst, sculk_shrieker, sculk_sensor, otherside:sculk_stone, otherside:tendril_block, otherside:tendril_heart (extend later with breakout/bloom anchors). NOTE SINGULAR tags/block/ path.
- **Shape**: VoxelShape union of node box + connected arm boxes. NO collision shape (entities pass through).
- **Stepping on its nerves**: `entityInside` — server players gain +2 Resonance/Attention with a 10t per-player cooldown. Walking along a vein line is LOUD to the beast.
- **Light**: base state light 1; CHARGED light 5.
- **Drops**: 15% echo dust (any tool); silk touch → itself.

## Pulse system (the readable signals)
`VeinNetwork.sendPulse(ServerLevel, BlockPos from, BlockPos to)`:
1. BFS across connected vein cords from→to (cap 256 nodes; fail silently if unreachable).
2. For path index i: schedule CHARGED=true at tick 4*i, CHARGED=false at 4*i+6. The 6t overlap vs 4t spacing yields a ~3-cell traveling pulse exactly like the preview mock.
3. Every 3rd segment that charges plays a soft low thrum (volume 0.3, pitch 0.7+0.1*rand).
4. Director log row: VEIN_PULSE, from, to, length.
Also `VeinNetwork.pulseOmni(ServerLevel, BlockPos origin, int radius)` — pulses outward along all branches (used for the beast's "flinch").

## Deferred to the Worldbeast spec (do NOT improvise)
Creep-toward-player growth, bell recoil/retraction, severing consequences, and what pulses MEAN (telegraphing surges/breakouts). This block is the canvas + the pulse API only. Record in DEVIATIONS.md.
