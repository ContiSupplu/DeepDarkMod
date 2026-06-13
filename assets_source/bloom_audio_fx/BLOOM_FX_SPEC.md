# Listening Bloom — Particles & Audio Spec (NeoForge 1.21.1)

Particles and sounds are MOD-SIDE (code-triggered), not bbmodel content. This spec keys every
effect to the exact keyframe timestamps in the embedded animations, so implementation = transcription.

## Sound files → assets/otherside/sounds/entity/bloom/
bloom_unfurl.ogg (2.8s) · bloom_fold.ogg (1.1s) · bloom_twitch1/2.ogg · bloom_alert.ogg (0.75s)
· bloom_tremor.ogg · bloom_presence_loop.ogg (3.85s, seamless)
These are SYNTHESIZED placeholders with correct character & timing — shippable for filming;
replaceable 1:1 later without code changes if better foley is sourced.

### sounds.json
```json
{
  "entity.bloom.unfurl":   {"sounds": [{"name": "otherside:entity/bloom/bloom_unfurl", "stream": false}], "subtitle": "subtitles.otherside.bloom.unfurl"},
  "entity.bloom.fold":     {"sounds": ["otherside:entity/bloom/bloom_fold"], "subtitle": "subtitles.otherside.bloom.fold"},
  "entity.bloom.twitch":   {"sounds": ["otherside:entity/bloom/bloom_twitch1", "otherside:entity/bloom/bloom_twitch2"], "subtitle": "subtitles.otherside.bloom.twitch"},
  "entity.bloom.alert":    {"sounds": ["otherside:entity/bloom/bloom_alert"], "subtitle": "subtitles.otherside.bloom.alert"},
  "entity.bloom.tremor":   {"sounds": ["otherside:entity/bloom/bloom_tremor"]},
  "entity.bloom.presence": {"sounds": [{"name": "otherside:entity/bloom/bloom_presence_loop", "stream": true}]}
}
```
Subtitles (lang): unfurl "Something unfurls" · fold "Something recoils" · twitch "Tendrils twitch" · alert "It hears you".

## Trigger table (timestamps = the embedded animation keyframes)
| Moment | Sound (vol, pitch) | Particles |
|---|---|---|
| UNFURL start | unfurl (0.9, 0.95–1.05) | Sculk dust falls from petal seams during 0–2.0s: 1–2 ASH-tinted `bloom_spore` per 2t at random petal tips. At t≈1.7s (feed rises): 6 SCULK_SOUL rising from membrane center, slow. Ground: VeinNetwork.pulseOmni(origin, 6) at t=0 — its arrival announces itself down the veins. |
| Sticky petal snap (t≈2.2s) | twitch (0.6, 0.8) | 3 SCULK_CHARGE_POP at that petal's tip |
| FOLD start | fold (1.0, 1.0) | During 0.26–0.9s: 10 SCULK_SOUL spawned in 1.2-block ring with INWARD velocity (sucked into the closing bud). At close (t≈0.9s): one SCULK_CHARGE burst at bud tip |
| TWITCH | twitch (0.5, 0.9–1.15) | 2–4 SCULK_CHARGE_POP at the twitching petals' tips + ONE `sound_ripple` ring traveling FROM the heard position TOWARD the dish (reuse the dimension spec's mandatory resonance-ripple particle — the bloom visualizes hearing) |
| SWEEP micro-correct (t=4.98s) | twitch (0.25, 1.3) | none — near-silence is the point |
| ALERT_LOCK flare (t=0.14s) | alert (1.0, 1.0) | 8 SCULK_CHARGE, one at each outer petal tip, simultaneous |
| ALERT_LOCK stillness (0.2–3.0s) | NOTHING. Suppress idle particles entirely during the lock. | Absence is the effect |
| Feeler tremors (idle t=9.1s, alert t=1.4/2.3s) | tremor (0.3, 1.0–1.4) | 1 WARPED_SPORE at that feeler tip |
| OPEN idle (continuous) | presence loop — see below | 1 SCULK_SOUL drifting up from membrane every 3–6s (randomized, never metronomic); rare WARPED_SPORE within 1.5 blocks |

## Presence loop handling
Client `AbstractTickableSoundInstance` bound to the entity while state=OPEN within 24 blocks:
volume = 0.45 * (1 - dist/24)^2, so it fades in as you approach — players FEEL proximity before
they consciously hear it. Stop on fold/death/dimension change. While any bloom's presence loop is
audible at >0.2 volume, duck MUSIC category to 60% (reuse the guardian AudioDuck).

## Custom particles to register (SimpleParticleType)
- `bloom_spore`: 4 tiny teal-gray motes texture variants, gravity 0.02, lifetime 30–50t, slight drift.
- `sound_ripple`: ALREADY REQUIRED by the dimension spec (resonance ripples) — one registration serves both; cross-reference, do not duplicate.

## Implementation notes
- Trigger one-shots from the SERVER via entity events (`level.broadcastEntityEvent`) so all clients hear/see consistently; spawn particles CLIENT-side in the handler.
- Particle positions at petal tips: approximate ring radius 1.3 blocks at entity height 0.55 at the petal's fixed yaw — adequate; exact bone-transform sampling unnecessary.
- Config: `bloomParticleDensity` 0–2 (default 1) and master `bloomAudioEnabled` — filming sometimes needs a silent set.
- Performance: peak particle count per bloom per event ≤ 12; idle ≤ 1 per 3s. Negligible.
