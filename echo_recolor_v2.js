/**
 * echo_recolor_v2.js — Node.js port of echo_recolor.py
 *
 * Palette sampled from sculk.png. Structural blocks use dark portion only (no cyan dots).
 * Ground (sculk_grass_top) keeps full sculk range + animation via .mcmeta.
 * Grass side uses vanilla grass_block_side_overlay for natural organic edge.
 */
const fs = require('fs');
const path = require('path');
const { createCanvas, loadImage } = require('@napi-rs/canvas');

const IN_DIR  = path.join(__dirname, 'build', 'tmp', 'vanilla_textures');
const OUT_DIR = path.join(__dirname, 'src', 'main', 'resources', 'assets', 'otherside', 'textures', 'block');

// Fallback sculk palette (approx vanilla sculk)
const FALLBACK = [[9,12,18],[16,26,33],[24,43,47],[38,74,73],[55,115,108],[82,170,156],[116,214,196]];
const STRUCT_LMAX = 0.55;  // structural blocks only use lower portion of palette

function luminance(r, g, b) {
  return (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
}

function sampleSculkPalette(imgData, w, h, n = 7) {
  // Sample ALL pixels from the entire flipbook (all frames), not just first 16x16
  const samples = [];
  for (let y = 0; y < h; y++) {
    for (let x = 0; x < w; x++) {
      const idx = (y * w + x) * 4;
      const a = imgData[idx + 3];
      if (a < 128) continue; // skip transparent
      samples.push({ r: imgData[idx], g: imgData[idx+1], b: imgData[idx+2],
                      L: luminance(imgData[idx], imgData[idx+1], imgData[idx+2]) });
    }
  }

  if (samples.length === 0) return FALLBACK;

  // Sort by luminance
  samples.sort((a, b) => a.L - b.L);

  // Pick n stops at percentile positions for good distribution
  const stops = [];
  for (let k = 0; k < n; k++) {
    const pct = k / (n - 1);
    const idx = Math.min(Math.floor(pct * (samples.length - 1)), samples.length - 1);
    // Average a small window around the percentile for stability
    const windowSize = Math.max(1, Math.floor(samples.length * 0.02));
    const lo = Math.max(0, idx - windowSize);
    const hi = Math.min(samples.length - 1, idx + windowSize);
    let rSum = 0, gSum = 0, bSum = 0, count = 0;
    for (let i = lo; i <= hi; i++) {
      rSum += samples[i].r; gSum += samples[i].g; bSum += samples[i].b; count++;
    }
    stops.push([Math.round(rSum/count), Math.round(gSum/count), Math.round(bSum/count)]);
  }

  // Check for collapse: if all stops are too similar, use fallback
  const range = Math.max(
    Math.abs(stops[0][0] - stops[n-1][0]),
    Math.abs(stops[0][1] - stops[n-1][1]),
    Math.abs(stops[0][2] - stops[n-1][2])
  );
  if (range < 30) {
    console.log('WARNING: palette collapsed (range=' + range + '), using FALLBACK');
    return FALLBACK;
  }

  return stops;
}

function buildLUT(stops, lmax = 1.0) {
  const lut = new Array(256);
  for (let i = 0; i < 256; i++) {
    const L = (i / 255.0) * lmax;
    const seg = L * (stops.length - 1);
    const j = Math.min(Math.floor(seg), stops.length - 2);
    const f = seg - j;
    lut[i] = [
      stops[j][0] + (stops[j+1][0] - stops[j][0]) * f,
      stops[j][1] + (stops[j+1][1] - stops[j][1]) * f,
      stops[j][2] + (stops[j+1][2] - stops[j][2]) * f,
    ];
  }
  return lut;
}

async function loadImageData(filePath) {
  const img = await loadImage(filePath);
  const canvas = createCanvas(img.width, img.height);
  const ctx = canvas.getContext('2d');
  ctx.drawImage(img, 0, 0);
  return { data: ctx.getImageData(0, 0, img.width, img.height), w: img.width, h: img.height, canvas, ctx };
}

function recolorData(imgData, w, h, stops, lmax = 1.0) {
  const lut = buildLUT(stops, lmax);
  const out = new Uint8ClampedArray(imgData.data);
  for (let i = 0; i < out.length; i += 4) {
    const L = luminance(out[i], out[i+1], out[i+2]);
    const idx = Math.max(0, Math.min(255, Math.round(L * 255)));
    const c = lut[idx];
    out[i]   = Math.round(c[0]);
    out[i+1] = Math.round(c[1]);
    out[i+2] = Math.round(c[2]);
    // alpha unchanged
  }
  return out;
}

function emissiveData(imgData, w, h, stops, thresh = 0.45) {
  const lut = buildLUT(stops, 1.0);
  const out = new Uint8ClampedArray(imgData.data);
  for (let i = 0; i < out.length; i += 4) {
    const L = luminance(out[i], out[i+1], out[i+2]);
    const idx = Math.max(0, Math.min(255, Math.round(L * 255)));
    if (L > thresh) {
      const c = lut[idx];
      out[i]   = Math.round(c[0]);
      out[i+1] = Math.round(c[1]);
      out[i+2] = Math.round(c[2]);
    } else {
      out[i] = out[i+1] = out[i+2] = 0;
    }
  }
  return out;
}

function savePixels(pixels, w, h, outPath) {
  const canvas = createCanvas(w, h);
  const ctx = canvas.getContext('2d');
  const imgData = ctx.createImageData(w, h);
  imgData.data.set(pixels);
  ctx.putImageData(imgData, 0, 0);
  fs.writeFileSync(outPath, canvas.toBuffer('image/png'));
}

function copyMcmeta(srcPath, outPath) {
  const mcmeta = srcPath + '.mcmeta';
  if (fs.existsSync(mcmeta)) {
    fs.copyFileSync(mcmeta, outPath + '.mcmeta');
    return true;
  }
  return false;
}

const STRUCT_MAP = {
  'dirt.png':             'sculk_dirt.png',
  'stone.png':            'sculk_stone.png',
  'gravel.png':           'sculk_gravel.png',
  'oak_leaves.png':       'sculk_leaves.png',
  'oak_log.png':          'echo_log.png',
  'oak_log_top.png':      'echo_log_top.png',
  'oak_planks.png':       'echo_planks.png',
  'stripped_oak_log.png':  'stripped_echo_log.png',
  'stripped_oak_log_top.png': 'stripped_echo_log_top.png',
  'oak_door_top.png':     'echo_door_top.png',
  'oak_door_bottom.png':  'echo_door_bottom.png',
  'oak_trapdoor.png':     'echo_trapdoor.png',
};

async function main() {
  // Sample palette from sculk.png
  const sculkPath = path.join(IN_DIR, 'sculk.png');
  let stops;
  if (fs.existsSync(sculkPath)) {
    const { data, w, h } = await loadImageData(sculkPath);
    stops = sampleSculkPalette(data.data, w, h);
    console.log('palette source: sculk.png');
    console.log('stops:', stops.map(s => `[${s.map(v=>Math.round(v)).join(',')}]`).join(' '));
  } else {
    stops = FALLBACK;
    console.log('palette source: FALLBACK');
  }

  // GROUND: sculk_grass_top = sculk recolored (full range) + carry animation
  if (fs.existsSync(sculkPath)) {
    const { data, w, h } = await loadImageData(sculkPath);
    const recolored = recolorData(data, w, h, stops, 1.0);
    const outPath = path.join(OUT_DIR, 'sculk_grass_top.png');
    savePixels(recolored, w, h, outPath);
    const hasMcmeta = copyMcmeta(sculkPath, outPath);
    console.log('✓ sculk_grass_top.png' + (hasMcmeta ? ' + .mcmeta' : ''));

    // Emissive map for ground
    const emissive = emissiveData(data, w, h, stops);
    const ePath = path.join(OUT_DIR, 'sculk_grass_top_e.png');
    savePixels(emissive, w, h, ePath);
    if (hasMcmeta) copyMcmeta(sculkPath, ePath);
    console.log('✓ sculk_grass_top_e.png' + (hasMcmeta ? ' + .mcmeta' : ''));
  }

  // STRUCTURAL: dark sculk palette (lower range only), no cyan dots
  let done = 0;
  for (const [src, dst] of Object.entries(STRUCT_MAP)) {
    const p = path.join(IN_DIR, src);
    if (!fs.existsSync(p)) { console.log(`  skip ${src} (not found)`); continue; }
    const { data, w, h } = await loadImageData(p);
    const recolored = recolorData(data, w, h, stops, STRUCT_LMAX);
    savePixels(recolored, w, h, path.join(OUT_DIR, dst));
    console.log(`✓ ${dst}`);
    done++;

    // Also generate sculk_grass_bottom from dirt
    if (src === 'dirt.png') {
      savePixels(recolored, w, h, path.join(OUT_DIR, 'sculk_grass_bottom.png'));
      console.log('✓ sculk_grass_bottom.png');
    }
  }

  // GRASS SIDE: dark sculk dirt + recolored vanilla overlay (natural organic edge)
  const basePath = path.join(IN_DIR, 'grass_block_side.png');
  const ovPath   = path.join(IN_DIR, 'grass_block_side_overlay.png');

  if (fs.existsSync(basePath) && fs.existsSync(ovPath)) {
    const base = await loadImageData(basePath);
    const ov   = await loadImageData(ovPath);

    // Recolor base in dark (structural) palette
    const baseRecolored = recolorData(base.data, base.w, base.h, stops, STRUCT_LMAX);
    // Recolor overlay in full palette (cyan fringe)
    const ovRecolored   = recolorData(ov.data, ov.w, ov.h, stops, 1.0);
    // Read raw overlay alpha to get vanilla's organic shape
    const ovRaw = ov.data.data;

    // Composite: where overlay alpha > 10, use overlay pixels
    const result = new Uint8ClampedArray(baseRecolored);
    for (let i = 0; i < result.length; i += 4) {
      if (ovRaw[i + 3] > 10) {
        result[i]   = ovRecolored[i];
        result[i+1] = ovRecolored[i+1];
        result[i+2] = ovRecolored[i+2];
        result[i+3] = ovRecolored[i+3];
      }
    }

    savePixels(result, base.w, base.h, path.join(OUT_DIR, 'sculk_grass_side.png'));
    console.log('✓ sculk_grass_side.png (overlay composite)');
  } else if (fs.existsSync(path.join(IN_DIR, 'dirt.png'))) {
    // Fallback: dirt with a few cyan specks near top
    const { data, w, h } = await loadImageData(path.join(IN_DIR, 'dirt.png'));
    const side = recolorData(data, w, h, stops, STRUCT_LMAX);
    const lut = buildLUT(stops, 1.0);
    const rng = (seed) => { let s = seed; return () => { s = (s * 1103515245 + 12345) & 0x7fffffff; return s / 0x7fffffff; }; };
    const rand = rng(1);
    for (let x = 0; x < 16; x++) {
      const maxY = 2 + Math.floor(rand() * 4);
      for (let y = 0; y < maxY; y++) {
        if (rand() < 0.7) {
          const idx = (y * w + x) * 4;
          const c = lut[200];
          side[idx] = Math.round(c[0]);
          side[idx+1] = Math.round(c[1]);
          side[idx+2] = Math.round(c[2]);
        }
      }
    }
    savePixels(side, w, h, path.join(OUT_DIR, 'sculk_grass_side.png'));
    console.log('✓ sculk_grass_side.png (fallback)');
  }

  console.log(`\nRecolored ground + ${done} structural blocks + grass side -> ${OUT_DIR}`);
}

main().catch(e => { console.error(e); process.exit(1); });
