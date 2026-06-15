/**
 * echo_recolor.js — Node.js recolor for Echo Dimension textures.
 * 
 * Handles animated flipbook textures (tall PNGs with multiple frames).
 * Produces .mcmeta for animated outputs + _e.png emissive maps.
 * 
 * Usage: node echo_recolor.js
 */
const fs = require('fs');
const path = require('path');
const { createCanvas, loadImage } = require('@napi-rs/canvas');

// === COLOR RAMPS: [shadow, mid, highlight] ===
// Sculk ramp: duotone — very dark base + VIVID CYAN dots. Not a smooth gradient.
const RAMPS = {
  sculk:         [[14,20,28], [30,40,50], [150,225,240]],
  stone:         [[24,28,34], [51,57,65], [102,113,126]],
  dirt:          [[26,26,30], [46,46,52], [86,86,94]],
  wood:          [[30,29,33], [55,54,60], [99,100,108]],
  wood_stripped: [[38,38,42], [66,66,72], [112,114,122]],
  leaves:        [[20,30,31], [44,60,60], [104,150,146]],
};

const MAPPING = {
  'sculk.png':               ['sculk_grass_top.png',       'sculk'],
  'dirt.png':                ['sculk_dirt.png',            'dirt'],
  'stone.png':               ['sculk_stone.png',           'stone'],
  'gravel.png':              ['sculk_gravel.png',          'stone'],
  'oak_leaves.png':          ['sculk_leaves.png',          'leaves'],
  'oak_log.png':             ['echo_log.png',              'wood'],
  'oak_log_top.png':         ['echo_log_top.png',          'wood'],
  'oak_planks.png':          ['echo_planks.png',           'wood'],
  'stripped_oak_log.png':    ['stripped_echo_log.png',      'wood_stripped'],
  'stripped_oak_log_top.png':['stripped_echo_log_top.png',  'wood_stripped'],
  'oak_door_top.png':        ['echo_door_top.png',         'wood'],
  'oak_door_bottom.png':     ['echo_door_bottom.png',      'wood'],
  'oak_trapdoor.png':        ['echo_trapdoor.png',         'wood'],
};

const GLOW_RAMPS = new Set(['sculk']);

function lerp(a, b, t) {
  return [
    Math.round(a[0] + (b[0] - a[0]) * t),
    Math.round(a[1] + (b[1] - a[1]) * t),
    Math.round(a[2] + (b[2] - a[2]) * t),
  ];
}

function colorMap(L, ramp) {
  const [s, m, h] = ramp;
  return L < 0.5 ? lerp(s, m, L / 0.5) : lerp(m, h, (L - 0.5) / 0.5);
}

/**
 * Recolor an entire image (including multi-frame flipbooks).
 * Returns { baseBuffer, emBuffer (or null) }
 */
function recolorBuffer(data, rampKey) {
  const ramp = RAMPS[rampKey];
  const out = new Uint8ClampedArray(data.length);
  const isGlow = GLOW_RAMPS.has(rampKey);
  const em = isGlow ? new Uint8ClampedArray(data.length) : null;

  for (let i = 0; i < data.length; i += 4) {
    const r = data[i], g = data[i+1], b = data[i+2], a = data[i+3];
    const L = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
    const mapped = colorMap(L, ramp);
    out[i]   = mapped[0];
    out[i+1] = mapped[1];
    out[i+2] = mapped[2];
    out[i+3] = a;

    if (em) {
      if (L > 0.55) {
        em[i]   = mapped[0];
        em[i+1] = mapped[1];
        em[i+2] = mapped[2];
        em[i+3] = a;
      } else {
        em[i] = em[i+1] = em[i+2] = 0;
        em[i+3] = a;
      }
    }
  }
  return { outData: out, emData: em };
}

function canvasToBuffer(canvas) {
  return canvas.toBuffer('image/png');
}

async function processTexture(inPath, outPath, rampKey, emPath) {
  const imgBuf = fs.readFileSync(inPath);
  const img = await loadImage(imgBuf);
  const w = img.width, h = img.height;

  // Draw full image (may be a tall flipbook for animated textures)
  const canvas = createCanvas(w, h);
  const ctx = canvas.getContext('2d');
  ctx.drawImage(img, 0, 0);
  const imageData = ctx.getImageData(0, 0, w, h);

  const { outData, emData } = recolorBuffer(imageData.data, rampKey);

  // Write base texture
  const outCanvas = createCanvas(w, h);
  const outCtx = outCanvas.getContext('2d');
  const outImgData = outCtx.createImageData(w, h);
  outImgData.data.set(outData);
  outCtx.putImageData(outImgData, 0, 0);
  fs.writeFileSync(outPath, canvasToBuffer(outCanvas));

  // Copy .mcmeta if source has one (animated textures)
  const mcmetaSrc = inPath + '.mcmeta';
  if (fs.existsSync(mcmetaSrc)) {
    const mcmeta = fs.readFileSync(mcmetaSrc, 'utf-8');
    fs.writeFileSync(outPath + '.mcmeta', mcmeta);
    console.log(`    + ${path.basename(outPath)}.mcmeta (animated)`);
  }

  // Write emissive if applicable
  if (emData && emPath) {
    const emCanvas = createCanvas(w, h);
    const emCtx = emCanvas.getContext('2d');
    const emImgData = emCtx.createImageData(w, h);
    emImgData.data.set(emData);
    emCtx.putImageData(emImgData, 0, 0);
    fs.writeFileSync(emPath, canvasToBuffer(emCanvas));
    // Emissive also needs .mcmeta if the base is animated
    if (fs.existsSync(mcmetaSrc)) {
      fs.writeFileSync(emPath + '.mcmeta', fs.readFileSync(mcmetaSrc, 'utf-8'));
      console.log(`    + ${path.basename(emPath)}.mcmeta (animated emissive)`);
    }
  }

  return { w, h, frames: h > w ? h / w : 1 };
}

async function main() {
  const inDir = path.join(__dirname, 'build', 'tmp', 'vanilla_textures');
  const outDir = path.join(__dirname, 'src', 'main', 'resources', 'assets', 'otherside', 'textures', 'block');

  console.log(`Input:  ${inDir}`);
  console.log(`Output: ${outDir}\n`);

  let done = 0;
  const missing = [];

  for (const [src, [dst, ramp]] of Object.entries(MAPPING)) {
    const inPath = path.join(inDir, src);
    if (!fs.existsSync(inPath)) { missing.push(src); continue; }

    const emPath = GLOW_RAMPS.has(ramp)
      ? path.join(outDir, dst.replace('.png', '_e.png'))
      : null;

    const info = await processTexture(inPath, path.join(outDir, dst), ramp, emPath);
    const frameStr = info.frames > 1 ? ` (${info.frames} frames, animated)` : '';
    console.log(`  ✓ ${src} → ${dst}${frameStr}`);
    done++;

    // dirt.png also produces sculk_grass_bottom
    if (src === 'dirt.png') {
      await processTexture(inPath, path.join(outDir, 'sculk_grass_bottom.png'), 'dirt', null);
      console.log(`  ✓ ${src} → sculk_grass_bottom.png`);
    }
  }

  // sculk_grass_side = echo dirt with sculk band on top (first frame only, not animated)
  const sculkPath = path.join(inDir, 'sculk.png');
  const dirtPath = path.join(inDir, 'dirt.png');
  if (fs.existsSync(sculkPath) && fs.existsSync(dirtPath)) {
    const sculkImg = await loadImage(fs.readFileSync(sculkPath));
    const dirtImg = await loadImage(fs.readFileSync(dirtPath));
    const sw = sculkImg.width;
    const dw = dirtImg.width, dh = dirtImg.height;

    // Get first frame of sculk (top 16x16 from the flipbook)
    const sculkCanvas = createCanvas(sw, sw);
    const sculkCtx = sculkCanvas.getContext('2d');
    sculkCtx.drawImage(sculkImg, 0, 0, sw, sw, 0, 0, sw, sw);
    const sculkData = sculkCtx.getImageData(0, 0, sw, sw);
    const sculkResult = recolorBuffer(sculkData.data, 'sculk');

    // Recolor full dirt
    const dirtCanvas = createCanvas(dw, dh);
    const dirtCtx = dirtCanvas.getContext('2d');
    dirtCtx.drawImage(dirtImg, 0, 0);
    const dirtData = dirtCtx.getImageData(0, 0, dw, dh);
    const dirtResult = recolorBuffer(dirtData.data, 'dirt');

    // Composite: top 5 rows from sculk, rest from dirt
    const sideData = new Uint8ClampedArray(dirtResult.outData);
    const bandRows = 5;
    for (let y = 0; y < bandRows && y < dh; y++) {
      for (let x = 0; x < dw; x++) {
        const dstIdx = (y * dw + x) * 4;
        const srcIdx = (y * sw + x) * 4;
        sideData[dstIdx]   = sculkResult.outData[srcIdx];
        sideData[dstIdx+1] = sculkResult.outData[srcIdx+1];
        sideData[dstIdx+2] = sculkResult.outData[srcIdx+2];
        sideData[dstIdx+3] = sculkResult.outData[srcIdx+3];
      }
    }

    const sideCanvas = createCanvas(dw, dh);
    const sideCtx = sideCanvas.getContext('2d');
    const sideImgData = sideCtx.createImageData(dw, dh);
    sideImgData.data.set(sideData);
    sideCtx.putImageData(sideImgData, 0, 0);
    fs.writeFileSync(path.join(outDir, 'sculk_grass_side.png'), canvasToBuffer(sideCanvas));
    console.log(`  ✓ composite → sculk_grass_side.png`);
  }

  console.log(`\nRecolored ${done} textures → ${outDir}`);
  if (missing.length) console.log('Missing vanilla inputs (skipped):', missing.join(', '));
}

main().catch(e => { console.error(e); process.exit(1); });
