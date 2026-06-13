# THE WORLDBEAST REWORK — Overworld Master Spec (v1.0)
### Replaces: design doc §"Surface Infection", §"Phases", §"HUD"; rewires §"Finale"
### Carries over unchanged: conversion map, budgeted frontier engine, echo economy, cleansing tools, storms/fog stack, Otherside dimension (M4), portal & guardian, region suppression via spires
### Build AFTER M4 Phase A-C are stable. All departures → DEVIATIONS.md as usual.

---

## 0. NAMES (constants — final names are a lang-file swap, do not hardcode strings)
| Constant | Working canon | Notes |
|---|---|---|
| BEAST_NAME | **The Silence** | the monster itself; revealed mid-movie, never in early subtitles |
| SITE_FIRST | **the Breach** | the first wound; proper noun; never fully closes |
| SITE_PASSIVE | **Sore** | passive surfacing (it noticed a place) |
| SITE_FEEDING | **Maw** | active feeding event (it is eating a place) |
| MANIFEST_NAME | **the Attention** | the listening blooms collectively; players may just say "It" |

## 1. THESIS & DESIGN LAWS (every system must satisfy all six)
The deep dark is the world's core — its true self. The Breach is not an infection arriving; it is the world waking. THE SILENCE wants a world that is dark, quiet, and still; players are the noise it intends to end.
1. **It is a creature, not weather.** Everything reads as one body: veins are nerves, Wardens are hands, blooms are its attention, Maws are its mouths, the Whisper is its voice, the Origin is its ear.
2. **It cannot be fought, only survived, starved, hurt, and finally put back to sleep.** Nothing of the beast is killable. Tentacles recoil; blooms fold; Maws seal. No health bars, no kill credit.
3. **It listens before it acts.** Every aggressive act is telegraphed through the body: vein pulse-trains converge, a bloom unfurls and watches, ambient life flees. An observant player (and audience) can always read it coming.
4. **No phases, no announcements.** Escalation is continuous and stat-driven. New behaviors simply HAPPEN; each system's first occurrence is its announcement and fires a FIRST_* director row (and is director-gateable).
5. **Fully diegetic.** NO gameplay HUD for any beast system (delete infection %, region strip; never add an attention meter on screen). The world state is read on camera through the body itself. Numbers go to the director log only.
6. **Emergent on the surface, authored underneath.** The organism runs free between beats; pacing rails cap world-scale escalation per week; every major beat is director-triggerable AND suppressible.

## 2. THE ORGANISM — WorldbeastState (level SavedData, overworld)
Persisted singleton + per-player attachment. Synced to ZERO client UI; director commands read it.

### 2.1 MASS — how much world it has reclaimed (0–100%)
- claimedChunks: a chunk counts when ≥60% of a 16-point top-layer sample is converted blocks. MASS = claimed / explored-or-bordering (reuse the fixed explored-chunk tracker; the old denominator bug must be confirmed dead before this builds).
- Drives: spread budget multiplier (1 + MASS/50), Maw max radius, silencing availability, ambient soundscape weight.
- **Pacing rails (LAW 6):** weekly soft caps on MASS, default {wk1:3, wk2:7, wk3:12, wk4:18, wk5+:+6/wk}. Above cap → global spread ×0.15 (it still creeps; it stops sprinting). Config `beast.massRails`, director `railsOverride`.

### 2.2 HUNGER / SATIATION — the breathing rhythm (0–100)
- HUNGER rises +0.2/min base, ×(1+MASS/40), ×(1+ACUITY/100). Falls only by EATING.
- **Nutrition table** (charge fed to frontier AND hunger reduction ×0.05/pt): stone-family 0.02, dirt 0.1, grass/sand 0.2, leaves 0.4, log 1.5, crop 2.0, flower 0.5, mushroom/fungus 1.0, animal 25, monster 8, villager 60, player-placed block ×1.5 bonus (it prefers what you made).
- Derived states: **RESTLESS** (≥70: surge orders likely, Maw eligible), **NEUTRAL** (20–70), **SATED** (<20 after a feeding session: ALL spread ×0.35, no aggressive orders, 1–2 in-game days; the quiet that lets episodes breathe — and unsettles).
- Consequence: rich biomes/farms accelerate it; deserts starve it. The map is a menu and players learn to read where it will lunge.

### 2.3 ACUITY — how smart it has become (0–100, monotonic)
- +1 per novel "taste": first villager, first player block, first bell cleanse, first Maw escape, first tendril severed, each biome type eaten. +3 per player death to it.
- Drives: telegraph lead-time SHRINKS (it gets cagier: pulse-train lead 180s at ACUITY 0 → 60s at 100), hunting-tendril speed, bloom tracking precision, silencing trigger threshold.

### 2.4 ATTENTION — per player (0–100): it knows where you are
Sources (server events): place light +1.5 (loud-light +4), block place/break within 24 of body +0.5, kill its drone +6, cleanse ground +15, bell ring +10, explosion +20, vein step +2 (existing), Maw escape +12, day spent inside claimed land +3. Decay −0.5/min while quiet AND >64 blocks from body; ×0 decay inside its body. **Bidirectional Listening coupling: gain ×(1 + Listening/100)** (§5.2).
| Tier | Range | Unlocks against that player |
|---|---|---|
| UNNOTICED | 0–25 | nothing; you are an ant |
| HEARD | 25–55 | vein growth reroutes toward your region; distant bloom sightings |
| KNOWN | 55–80 | hunting tendril dispatch; blooms manifest near you, snap-tracking; a Sore may erupt within 48 of your base |
| HUNTED | 80–100 | Maw eligible near/under your base; silencing wave at your base; bloom inside your walls |
Tier transitions fire ATTENTION_TIER rows; downward transitions are slow (the beast does not forget quickly: tier floor decays 1 tier/2 days).

## 3. THE BODY

### 3.1 Frontier engine rework (modify, don't rewrite)
Keep: conversion map, charge budget, per-tick block caps, light-slows rule, noise-feeds rule, player-block resistance (×0.25, armed only at MASS≥12 replacing old Phase-3 gate).
Add two weightings to frontier candidate scoring:
- **Appetite:** candidate score ×(1 + nutrition(block)/2). The front visibly lunges along forests/farms and crawls across stone.
- **Player gravity:** ×(1 + 0.6·proximityFactor) toward chunks within 96 of any player's logout/active position, scaled by that player's ATTENTION/100. Leave for three days; return to find the front has leaned toward home.
Feeding events (every block converted) report nutrition to HUNGER and to the regional charge pool (rich meals = local acceleration: eating makes it stronger exactly where it ate).

### 3.2 Veins — the nervous system (block exists; THIS defines meaning)
The cord block + pulse API are built (sculk_veins.zip). Canon behaviors:
- **Growth:** vein lines extend 1 block per 20–40s along the surface from body toward current ORDERS' targets (visible intent). Hunting tendrils are a faster special case (§4.2). Growth is the reroute mechanic for HEARD tier.
- **Pulse signals are ORDERS** traveling body→site. Signal types & meaning (speed 4t/segment, existing):
| Signal | Visual | Means | Lead time |
|---|---|---|---|
| SURGE | 3-pulse train | regional front acceleration ×3 for 90s, arrives at train end | 180s→60s by ACUITY |
| BREAKOUT | 5-pulse train ×≥3 converging lines | a Sore (or Maw if RESTLESS) erupts at the convergence | 120s→45s |
| RETALIATION | rapid 2-2-2 stutter | post-cleanse response (§6) inbound to the cleansed scar | 60s |
| FLINCH | pulseOmni outward | pain broadcast; cosmetic + drone agitation | instant |
- **SEVERING (the strategy layer):** breaking ≥6 cord blocks on a path interrupts any in-flight order on it → order DELAYED 5 min and visibly reroutes (new growth). Severing ALL paths to a target cancels the order and adds +8 ATTENTION to the nearest player (it felt that). Severed ends twitch (charged flicker 3s) and regrow toward reconnection at growth rate.

### 3.3 SORES — passive surfacings (it is under everything)
- **Trigger scoring** (per loaded surface chunk, 1/min): score = placedLightCount×2 + noiseEvents(10min) + livestockCount×3 + villagerCount×8, ×(1+MASS/60). Top scorer above threshold 40 AND ≥160 blocks from existing sites → BREAKOUT order issued (telegraph per §3.2).
- **Eruption:** 12s sequence — ground trembles (crack particles + tremor sfx) → 7×7 sculk burst with block-break debris → 1 shrieker + 2 sensors + 3–6 vein lines crawling outward + 2 bloom BUDS (0.4 scale, closed). Director row SORE_ERUPT.
- Sores spread as a normal (small-budget) front and serve as future Maw anchors. Cleansing a Sore = bell/purge as usual → counts as PAIN (§6).
- **The point:** no place is safe because the body is the planet. First Sore at a player base is a FIRST_* beat — gate it for the film.

### 3.4 MAWS — feeding events (the consumption centerpiece)
**Trigger:** HUNGER ≥70 AND a target region (Sore-anchored or Breach-adjacent) with nutrition density ≥ threshold AND converging BREAKOUT telegraph completed. Director can force/suppress. Max 1 active Maw; cooldown 1 in-game day.
**Opening (20s):** silencing brush (flames within 32 die in a wave) → ambient mobs flee → tremor build → terrain collapses inward over a 7-block throat → 3–5 maw_tentacles EMERGE (existing entity/anims: stagger their `emerge` starts 0.6s apart — never synchronized) → presence drone begins.
**Feeding session (60–120s, HUNGER-scaled):**
- **Grasp field:** radius R = 12 + 12×(HUNGER/100). Entities inside feel pull a = 0.012×(1−d/R)² blocks/t² toward the throat (capped 0.35 b/t). Trivial at rim — sprint-out always possible at rim; lethal near throat.
- **Debris consumption:** rim blocks detach 2–5/s (budget), become falling-debris display entities spiraling inward-down, swallowed at the throat (nutrition credited; concurrent debris cap 40; trees detach whole as 4–6 grouped entities). The land visibly collapses into a growing pit. Mobs dragged under are wrapped (brief sculk cocoon block) and credited.
- **Tentacles:** cycle idle_sway; STRIKE at entities 4–9 from throat; GRAB per README (drag 0.15 b/t; escapes: deal 8+ damage → wounded_recoil releases, ally bell pulse, amethyst-cluster-in-hand PRE-anchor immunity).
- **Player swallowed** (within 1.5 of throat): custom damage type `otherside:swallowed`, death message "%s was swallowed by the world". Respect keepInventory; drops scatter at the scar.
**Closure:** throat seals → scarred sinkhole (9–15 block sculk-lined bowl, permanent) → SATED state begins → tentacles `retract` (staggered). Director rows MAW_OPEN/MAW_CLOSE with nutrition total.
**Counterplay:** sever converging veins pre-open (delays/diverts per §3.2); bell pulse on an OPEN Maw forces closure in 8s (tentacles wounded_recoil → retract) but guarantees RETALIATION (§6) and +10 ATTENTION; amethyst anchor as above.
**Performance budget:** debris ≤40 entities, tentacles ≤5, particle caps per bloom spec; all constants config.

### 3.5 Base dismantling — the siege upgrade
When the front contacts player-placed structures (MASS≥12): veins climb the wall (multiface visual variant or cord lines at base), and every 30–80s ONE structural block is PULLED — becomes a falling block arcing toward the front line and absorbed (nutrition ×1.5). Walls crumble outward toward it; bases are not repainted, they are EATEN. Rate scales with owner's ATTENTION tier (HUNTED: every 15–30s). Amethyst blocks in the wall are never pulled and pause pulls within 4 blocks (counterplay carryover).

## 4. THE SENSES

### 4.1 Hunting tendrils (KNOWN tier)
A single feeler vein line dispatched at a specific player: extends 1 block per 4–8s (ACUITY-scaled) along the surface toward the target's live position, leaving a thin infection trail, rerouting as they move. Outwalkable; relentless. On reaching within 8 blocks of the target: ground bulges, ONE maw_tentacle at 0.6 scale performs the finisher — emerge → single GRAB attempt → retract regardless of outcome (8s total). Severing the feeler ≥16 blocks behind its tip kills the hunt (+8 ATTENTION). Rows TENDRIL_DISPATCH / TENDRIL_GRAB. First dispatch is a gateable FIRST_*.

### 4.2 Listening blooms — the Attention manifested (asset complete: model v4, 6 anims, audio zip, FX spec)
- **Spawn rules:** only on its body (claimed sculk). Frequency/distance by target's tier — HEARD: 40–80 blocks, brief; KNOWN: 16–40, long dwell, snap-tracking (head lerp 0.3 vs 0.12); HUNTED: may unfurl inside base perimeter. Always via `unfurl`, never pop (asset law). Max simultaneous per player: 1 (+1 ambient per 6 chunks of deep body).
- **Behavior loop:** unfurl → idle_listening → track target (head yaw/pitch) → twitch at heard game events (nearest petals + click) → if target silent >8s: sweep → player within 6 blocks: `alert` shriek + `fold` + despawn... and somewhere distant, something answers (sound only).
- **Grammar (LAW 3):** a bloom watching a location for >30s means an order is coming there — pair bloom dwell with the relevant pulse-train so the audience learns the tell.
- **First manifestation is a staged beat:** gated OFF by default; the director enables it after the first cleansing — you hurt it, and the world's response is not violence but attention: one bloom on a ridge, watching the player who rang the bell. FIRST_MANIFESTATION row.

## 5. THE VOICE & THE SILENCING

### 5.1 Silencing waves (HUNTED tier or director)
The "fed up with light and noise" made mechanical: from a body point within 48 of the target base, a wave expands at 0.5 b/t to radius 24–48: every flame-family light it touches dies in sequence (reuse extinguish tech: torches→extinguished variants, fires out, campfires/candles unlit), passing a 3s Darkness brush over players. Light can be replanted immediately — replanting against the tide IS the gameplay loop. Limit 1 per region per 2 days. Ambient creatures within the radius flee 10s BEFORE the wave (the birds going quiet is the tell). Row SILENCE_WAVE.
Deep-body audio law (client): inside heavily claimed regions, ambient/music duck 40%, player sound effects gain a subtle muffle/distance filter — the world swallowing sound. Reuse AudioDuck; config `beast.audioMuffle`.

### 5.2 THE LISTENING — the madness (client-side sensory lies, mythic framing)
Hidden per-player meter 0–100: +1/15s while on claimed land, ×2 in the Otherside, ×3 within 64 of the Heart. Decay −1/20s outside, −15 per sleep on cleansed/unclaimed ground, −5 per minute holding amethyst. **Coupling: see §2.4 — the longer you're inside it, the more you hear it, and the more it hears you.**
| Stage | Threshold | Effects (ALL client-side, mechanically harmless — LAW: the beast lies to your senses, never to your world) |
|---|---|---|
| S1 | 25 | the Whisper addresses you in second person; line frequency up |
| S2 | 50 | phantom audio: footsteps behind (35% weight), door creak at home, creeper hiss with no creeper, your lantern's fuel-gasp when it's full; 1 event per 60–150s, never twice the same within 5 min |
| S3 | 75 | visual lies: shadow figure at vision's edge (despawns when faced, 8–20s exposure), vein patches on walls that aren't there (fade on approach), distant Warden silhouette dissolving into fog |
| S4 | 92 | it speaks: Whisper lines reference run facts (bed coordinates abandoned, lantern fuel, death count, "you left the lantern at home") — assembled from real data, delivered as its voice |
Hard rules: no hallucination ever damages, desyncs, spawns, or moves anything server-side; subtitles label phantom audio only as "?" (never confirm); `beast.listeningIntensity` 0.0–1.0 client config + master OFF (accessibility + filming retakes); Whisper text stays mythic — the beast's voice in your ears, never a depiction of mental illness. Stage transitions row LISTENING_STAGE.

## 6. PAIN & COUNTERPLAY
- **Pain events:** bell cleanse, purge charge, Maw force-close, spire destroyed (Otherside). Response: immediate FLINCH (pulseOmni — every vein in radius contracts; drones agitate 10s), then RETALIATION order: regional front ×2 for the next day + 1 Sore attempt near the scar + drones converge on the scar once. Pain is never free; it is also never fatal to press — the beast can be hurt into delay everywhere, killed nowhere (endings unchanged: Cleansing puts the world back to sleep / Sculkborn freezes the scar).
- **Bait & lure (emergent, protect it):** because appetite and attention are real systems, players CAN drive livestock to decoy sites, ring bells to drag the front off a village, go silent/dark to drop tiers. Never patch these out; they are the smart-play layer.
- **Amethyst doctrine (one rule everywhere):** clusters anchor against Maw pull and grab; blocks halt dismantling locally; holding it slows Listening; resonance repels conversion (carryover).

## 7. PRESENTATION (LAW 5)
- DELETE: infection % HUD, region strip, any meter. KEEP diegetic stack: breach storms, fog/gloom, ground mist (now keyed to HUNGER/ATTENTION as "moods": RESTLESS darkens regional fog ceiling +20%, SATED clears it).
- The FIRSTS (each fires FIRST_* row, each director-gateable): first Sore, first Sore at a base, first Maw, first swallow, first tendril hunt, first manifestation, first silencing wave, first S3 hallucination, first base block eaten, first severed order.
- Sound: every order arrival/Maw/wave reuses the established teal-monochrome audio language; the reserved ACCENT (color AND a new instrument timbre) still spends ONLY on the Origin.

## 8. DIRECTOR & DATA
Commands: `/otherside beast status | mass set <v> | hunger set <v> | acuity set <v> | attention get|set <player> <v> | listening get|set <player> <v> | maw open <pos>|close | sore spawn <pos> | tendril hunt <player> | silence wave <pos> | bloom spawn <pos> [scale] | order surge|breakout <pos> | gate <FIRST_id> on|off | rails override <pct> | suppress <system> <minutes>`.
CSV rows (append to existing log): BEAST_DAILY (day, mass, hunger, acuity, top attention), MAW_OPEN/CLOSE(+nutrition), SORE_ERUPT, SURGE_ORDER, BREAKOUT_ORDER, RETALIATION, ORDER_SEVERED, TENDRIL_DISPATCH/GRAB, SWALLOWED, SILENCE_WAVE, BLOOM_MANIFEST/FOLD, ATTENTION_TIER, LISTENING_STAGE, FIRST_*. Daily top-down claimed-map PNG render to `directors_log/maps/` for post-production graphics.

## 9. RETCONS & LORE WIRING
- **The Origin is the EAR of the world** — the first listener; the blind Phase-C fight is now the thesis (it takes your sight and makes you fight on its sensory terms). Origin boss bar uses style 1 + the reserved accent. Its chamber contains the only colossal bloom whose bioluminescence runs the accent color.
- **The Otherside is its interior** (lang strings: "you are not somewhere else. you are inside."); spires = nerve clusters (destroying one = severing at organ scale → region suppression, unchanged mechanically); the Vast One bloom appears ONCE in the void (distant, half-furled, turns toward noise, fog takes it).
- **Corrupted mobs are its drones** — rename tags/strings from "corrupted" to "drone" framing; behavior unchanged; they agitate on FLINCH and converge on RETALIATION.

## 10. MIGRATION, CONFIG, MILESTONES
**Delete:** phase state machine + phase presentation sequences + title cards; infection HUD + region strip; day-clock escalation. **Keep** (re-keyed to stats as noted): everything in the header carryover list.
**Config groups:** `[beast]` massRails, hungerRate, nutritionScale, attention sources/decay, tier thresholds, telegraph leads; `[maw]` radius, pull, debrisBudget, sessionLength, cooldown; `[listening]` rates, stage thresholds, intensity default; `[silencing]` radius, speed, cooldown; all director-suppressible.
**Milestones:** W1 WorldbeastState + frontier re-weighting + rails (verify: hunger/sated cycle observable; front leans toward base in 3 days). W2 vein orders + severing + Sores (verify: read a telegraph, sever it, watch reroute). W3 Maws + tentacle wiring + dismantling (verify: full Maw lifecycle on a test forest; escape all three ways; budget holds). W4 Attention + blooms + tendril hunts (verify: tier ladder by deliberate noise; bloom grammar; hunt outwalked and severed). W5 Silencing + Listening + director/CSV/map + FIRST gates (verify: full filming dry-run with all gates closed, then opened one by one).
