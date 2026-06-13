# Warden Boss Bar (Gemini art, converted) — Geometry & Rendering (NeoForge 1.21.1)

## Files → assets/otherside/textures/gui/bossbar/
- warden_bossbar_frame.png — 320×58 ornamental frame, transparent bar window, TEXT IS BAKED IN
- warden_bossbar_bg.png    — 293×12 empty-bar strip
- warden_bossbar_fill.png  — 293×12 fill strip (crop left portion by health %)

## Geometry (frame space)
FRAME 320×58. Window interior: x 14..306, y 26..37 (BW=293, BH=12).
Draw order: bg → fill cropped to round(293*progress) → 1px (190,255,242) vertical line at the crop edge (only when 0<pct<1) → frame on top.

## IMPORTANT: name text is baked into the art
The plaque already reads "WARDEN / DEEP DARK GUARDIAN". For style 0, DO NOT render the boss event's name text — skip the name draw entirely. (Consequence: this skin is specific to the Guardian; other bosses need their own frame art. The style byte already supports that.)

## Hook (unchanged from prior README)
CustomizeGuiOverlayEvent.BossEventProgress → if UUID is styled: cancel vanilla, blit at the vanilla anchor with x offset (182-320)/2 = -69 to stay screen-centered. The frame extends ~20px below the vanilla bar line (plaque + drips): expose client config `bossBarYOffset`, verify clearance against HUD mods. Render 1:1 with no stretching. Stack spacing for multiple styled bars: +66px.
## Styling payload (unchanged)
StyledBossBarPayload(UUID, style): style 0 = this skin (Portal Guardian only, since the name is baked). style 1 reserved for the Origin (its own art, finale accent color).
## Guardian wiring (unchanged)
ServerBossEvent, proximity add/remove 32 blocks, setProgress per tick, payload on creation.
