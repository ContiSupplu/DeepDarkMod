# THE ECHO DIMENSION — Master Design Doc (v1)

**Status:** new canon. This document **replaces** the old Otherside-dimension content (barren abyss
biome + void gen). It does **not** touch the overworld Worldbeast systems (W1–W5) or the portal
*framework code* — those are kept. We are rebuilding what's *inside* the dimension from square one.

Project: "The Otherside" — NeoForge 1.21.1, package `com._jackoboy.otherside`, repo
`ContiSupplu/DeepDarkMod` (branch main). Built for a YouTube movie. Director = the user (non-coder);
AI coder = Gemini; the assistant writes specs, audits committed source, and converts assets.

---

## 1. THE PREMISE (one line)
The Echo Dimension is a **shadow-mirror of the overworld** the Silence has grown into — same land,
wrong and luminous — where its **Core** beats. The player crosses over to reach and end the Core, in
a world that hunts them, with one fragile thing that hunts *for* them.

---

## 2. LOCKED CANON (confirmed) + OPEN PICKS
**Locked this session:**
- The dimension is a **parallel mirror of the overworld**, not the beast's literal interior. (Chosen
  because a 1:1 overworld replica only makes sense as a mirror-world, not an interior.)
- **Terrain = exact mirror of natural overworld terrain** — same shape, reskinned in echo blocks.
  Natural terrain only; player-built structures do **not** mirror (live-copying builds is out of scope).
- **Corruption is the spine** — one real system linking allies, animals, the villain artifact, and
  the traitor arc. Source = the Core/beast. Ward/cure = **amethyst + light** (existing mod doctrine).
- **One unified arc:** ending the Core is what **stops the overworld destruction.** The dimension is
  the campaign's climax, not a side-area.
- **Dimension renamed: the Echo Dimension** (resolves the old "Otherside" name collision with the
  *Deeper and Darker* mod).
- The roaming hunters here are the **Echo Souls/Spirits** (already built — reused as natural spawns).
- **Safe havens** via the existing Echo Lantern / Echo Anchor assets (light-wards that keep spirits off).

**Resolved this session:**
- Portal field color: **sculk-teal/cyan** (the screenshot's *look* — cloudy upward-scrolling field —
  recolored to sculk teal, not gold→green).
- Whispering Echo food: feeding **heals + raises loyalty**. It does **not** cleanse corruption —
  **only a golden apple cleanses corruption.**
- The Core is **both open and gated:** reachable and *heard* from arrival (the heartbeat guides you
  from the start), but **ending it requires progression** — it's protected/sealed; you must prepare
  (echo gear) and/or clear its safeguards before it can be put down. Always know where; can't rush it.

---

## 3. THE WORLD

### 3.1 Terrain — the mirror
The Echo Dimension generates the **same terrain shape as the overworld** (same seed + identical noise
generation), then every block is swapped to its echo counterpart at generation. So the coastline,
the mountain, the cave you know are all there — recognizable, wrong, glowing. (Implementation note for
Gemini: share the overworld seed and overworld-equivalent noise settings so shape matches; apply echo
block substitution via surface rules + a generation block-replacement pass. "Mirrors THIS overworld's
shape" depends on matching seed + gen settings.)

### 3.2 Block palette (assets delivered — `assets_source/echo_blocks/`)
| Overworld | Echo replacement |
|---|---|
| grass block | `sculk_grass` (top/side/bottom) |
| dirt | `sculk_dirt` |
| stone / deepslate | `sculk_stone` |
| gravel | `sculk_gravel` |
| oak log | `echo_log` (+ `echo_log_top`) |
| oak planks | `echo_planks` |
| leaves | `sculk_leaves` |
| water | **bright bioluminescent blue echo-water** (see 3.3) |

All dark with teal/green veining; leaves and grass carry an emissive fleck. This is the **new wood
type** ("echo wood") — full wood set derives from `echo_log`/`echo_planks` (stripped log, stairs,
slab, fence, door, etc.).

### 3.3 Water — the glow
Echo-water flows a **bright bioluminescent blue** (reference: image 1 — glowing blue shoreline).
Emissive/luminous, distinct from overworld water; it is the dimension's signature accent against the
dark land. Custom fluid (the existing animated `echo_fluid` asset is the basis; recolor to the bright
blue).

### 3.4 Sky — custom skybox
Custom dimension sky (reference: image 3): a **dusky purple-grey overcast** with **drifting green
spore-lights** floating through the air. No sun/moon disc; soft directionless gloom. Implemented via
dimension effects + a sky renderer + ambient floating particles.

### 3.5 Atmosphere (keep + retune the existing strengths)
The old dimension's fog/ambience was its best feature — carry it: oppressive teal fog, limited
visibility, ambient loop, the submerged feeling. Retune fog color to sit under the new sky.

---

## 4. THE PORTAL (completely new look)
Not a reskin of the vanilla swirl — a **new portal block** modeled on the screenshot (image 2):
- **Field:** a luminous, **cloudy/fog-like energy plane** — bright **gold at the top fading down to
  green at the bottom**, soft mottled clouds, strongly emissive, recolored to **sculk teal/cyan** (the screenshot's cloudy field, in sculk colors).
  **Animated: the clouds scroll/rise upward** continuously.
- **Frame:** dark **chiseled deepslate / sculk-brick** with repeating engraved motifs (image 2's
  patterned border), lit teal by the field's glow. New frame block, not vanilla.
- **Framework kept:** the existing portal *code* (frame detection, ignition, re-ignition tracking,
  the guardian-state machine NONE/ACTIVE/DEFEATED) stays. We replace the **block + texture + frame
  block + animation**, not the logic. Ignition reagent/recipe TBD (keep current or re-theme).

---

## 5. CORRUPTION — the spine system
A single, real mechanic that everything else plugs into.

- **What it is:** a 0–100 corruption value on Whispering Echoes (and applicable to tamed companions
  and, narratively, to players). At threshold, the subject **turns** — a Whispering Echo plays its
  `corrupt` clip and becomes hostile (uses `attack`).
- **Sources (raise corruption):** proximity to the Core; the **villain artifact**; being near
  corrupted entities; (option) prolonged darkness. Tunable per source, config.
- **Cure / ward (lower or block corruption):** **amethyst** (cleanse a corrupted echo, ward a zone)
  and **light** — carrying the existing mod doctrine forward. Feeding (see 7) may also cleanse.
- **Why it matters:** this is the movie's conflict in one stat. Good players keep their echoes clean;
  the traitor flips them. Corrupted animals, corrupted echoes, and the artifact are all the same
  machine.

---

## 6. INHABITANTS

### 6.1 Echo Spirits (Echo Souls — already built)
Reused as the dimension's **roaming hunters** — natural spawns that seek living players and attack.
The existing Echo Soul entity (state machine, teleport, leash, scream/dissipate) drops straight in as
the ambient threat. Light/echo-anchors deter them (safe havens, §9).

### 6.2 Corrupted animals (models TBD — user provides)
Reskinned passive mobs that have **turned** — hostile, corruption-themed variants. Same corruption
system (§5): they are animals at the far end of the corruption scale. Build when models arrive.

---

## 7. THE WHISPERING ECHO (the ally)
Asset: `Whispering_Echo.bbmodel` (small, 2-cube wisp, 32×32). Animations already rigged:
`idle` (loop), `attachment` (once — bonds to you), `follow` (loop), `rest` (loop), `death` (hold),
`corrupt` (hold — the turn), `attack` (once). A complete companion in seven clips.

- **Taming:** via **plant life found in the dimension** (§8) — the player uses the flora to bond an
  echo (`attachment` clip), not vanilla breeding. Fragile bond.
- **Roles (the value it gives the player):**
  - **Guide** — leads the player toward the Core (or toward safety); the Core is *heard* (§10), the
    echo points the way.
  - **Protect** — engages/distracts Echo Spirits for the player (`attack` used defensively).
  - **Alert** — the listening-in-reverse payoff: it senses danger (nearby spirits, corruption, the
    Core's pulse) and warns the player. Your monster listens for the player; now the player has
    something that listens for the monster.
- **Corruption (§5):** the bond can fail — proximity to the Core or the villain artifact corrupts it;
  it turns (`corrupt` → hostile `attack`). The player manages this with amethyst/light and feeding.
- **Food:** see §8 — eaten by the player *and* fed to echoes (loyalty / heal / cleanse — open pick).

---

## 8. FLORA & FOOD
- **Echo flora:** new plant life native to the dimension. Two jobs: the **taming reagent** for
  Whispering Echoes, and the base for **food**.
- **Echo food:** an edible item the **player eats** (buff/sustenance) *and* **feeds to Whispering
  Echoes** (loyalty/heal/cleanse — pick in §2). Ties flora → taming → ally upkeep into one loop.

---

## 9. PROGRESSION & GEAR
- **Echo wood** (§3.2): full wood set for tools/building — the early tier.
- **Echo-power armor & weapons (assets TBD — user deciding):** distinct sets/weapons each granting a
  different **Echo Power.** Placeholder power ideas to react to later (not locked): echo-sight (see
  spirits/Core through walls), silence (muffle your footsteps so spirits can't hear you — ties to the
  listening theme), echo-channel (buff/command nearby Whispering Echoes), warded (slows corruption).
  Built when models are chosen; the assistant can generate options if asked.
- **Safe havens:** the existing **Echo Lantern** / **Echo Anchor** assets become placeable
  light-wards that keep Echo Spirits at bay — defensible camps in a hostile world (carries the
  light-deters-dark doctrine).

---

## 10. THE CORE — the endgame
The **Core** is the win condition: a one-of-a-kind boss/structure that, when ended, **stops the
overworld destruction.**
- **Heard before found (Law 3):** the Core emits a **heartbeat pulse** sensed from across the world;
  it grows as the player nears. The Whispering Echo's *guide* role keys off this.
- **The fight / objective:** TBD (boss entity vs. ritual-destruction of a structure vs. multi-stage).
  Corruption pressure is highest near it (echoes turn here — the player may arrive alone).
- **Payoff:** ending it halts the overworld Worldbeast's consumption — the two halves resolve as one
  arc.

---

## 11. THE VILLAIN ARTIFACT (the betrayal device)
A **one-of-a-kind item** built for the movie's turn, when a player goes evil and serves the beast.
- **Powers:** **corrupt Whispering Echoes** (flip other players' allies against them via §5) and
  **command Echo Souls** to attack chosen players. The traitor becomes the beast's hand.
- **Counterplay (asymmetric balance):** the good players answer with **amethyst** — cleanse flipped
  echoes, ward zones, and resist the artifact's corruption — plus **light** safe-havens (§9). The
  artifact spreads corruption; amethyst/light hold it back. This is the on-screen tug-of-war.
- **Acquisition / where it lives:** TBD (tied to the Core, or a separate relic). Single instance.

---

## 12. HOW IT CONNECTS TO THE OVERWORLD ARC
- **Overworld** = the Silence's body, consuming the world (the Worldbeast systems W1–W5 — kept).
- **Echo Dimension** = its mirror-domain, where the **Core** drives that consumption.
- **The link:** end the Core → the overworld destruction stops. The traitor's artifact serves the
  Core's side; the good players' amethyst/light/echoes serve the world's side. One war, two stages.

---

## 13. DESIGN LAWS (carried — binding tiebreakers)
The Six Laws still govern: (1) creature-with-intent, (2) unfightable/only-deferrable [the Core is the
*one* sanctioned kill — it's a mirror-heart, not the beast itself], (3) telegraph-before-acting
(the Core's heartbeat; echoes turning is foreshadowed by rising corruption), (4) firsts-fire-once
(`FIRST_*` director rows), (5) fully-diegetic / no-HUD (corruption, loyalty, Core distance are felt
and heard, not shown — director log only), (6) emergent + authored rails. Amethyst doctrine, light
deters dark, additive-over-baked animation, singular tag paths (`tags/block`, `tags/item`), and the
`[DIR]` director-log workflow all carry over.

---

## 14. BUILD PHASING (world-first → film fast)
1. **The world** (asset-ready now): mirror gen + echo block palette + echo-wood set + bright-blue
   echo-water + custom skybox + retuned fog + **the new portal** (block/frame/animation). Echo Spirits
   already roam it. → You can *film* in the dimension at the end of this phase.
2. **The Whispering Echo** (model ready): entity + the 7 anim states + taming + guide/protect/alert +
   corruption hookup.
3. **Flora & food:** the dimension plant + the edible/feed item; close the ally-upkeep loop.
4. **Gear:** echo tools, then echo-power armor/weapons as models land; safe-haven lantern/anchor wiring.
5. **The Core:** heartbeat telegraph + the boss/objective + the overworld-stop payoff.
6. **The villain artifact:** corrupt-echoes + command-souls + amethyst/light counterplay.
   (Corrupted animals slot into phase 2–4 as models arrive.)

---

## 15. ASSET INVENTORY
**Delivered / ready:**
- Block palette: `echo_log`, `echo_log_top`, `echo_planks`, `sculk_grass` (3), `sculk_dirt`,
  `sculk_stone`, `sculk_gravel`, `sculk_leaves`.
- `Whispering_Echo.bbmodel` (32×32, 7 anims) — needs Java model conversion (apply the **+24 feet-line
  drop**; no flip — converter debt noted).
- Echo Soul entity (built) — reused as roaming spirit.
- Echo Lantern / Echo Anchor / Echo Fluid (recolor fluid to bright blue) — existing.
- References: image 1 (blue water), image 2 (portal), image 3 (skybox).

**Blocked on user-provided assets:**
- Echo-power armor & weapons (models — user deciding; assistant can generate if asked).
- Corrupted animal models.
- Portal frame block texture + animated field texture (to be made to match image 2).

---

## 16. OPEN DECISIONS / TODO
- Confirm: dimension = parallel mirror (assumed locked); terrain mirror approach (assumed locked).
- Core fight type (boss vs ritual vs multi-stage) — gated+open is locked; *how* you end it is open.
- Villain artifact: where it's acquired; single-instance enforcement.
- Portal ignition reagent (keep current vs re-theme).
- Old dimension teardown: remove `the_abyss` biome + abyss noise gen + old spawners; keep portal
  framework; migrate fog.
