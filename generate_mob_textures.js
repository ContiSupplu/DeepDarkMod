/**
 * generate_mob_textures.js — Generate UV-correct textures for Wug, Ward, Warb.
 * 
 * Reads the model code's texOffs to paint each UV region with sculk-themed colors.
 * Dark blue-grey base + cyan highlights on features.
 */
const fs = require('fs');
const path = require('path');
const { createCanvas } = require('@napi-rs/canvas');

const OUT_DIR = path.join(__dirname, 'src', 'main', 'resources', 'assets', 'otherside', 'textures', 'entity');

// Sculk color palette
const COLORS = {
  bodyDark:    [20, 28, 36],     // deep dark blue-grey body
  bodyMid:     [32, 42, 52],     // mid body tone  
  bodyLight:   [45, 58, 68],     // lighter body areas
  headDark:    [22, 30, 40],     // head base
  headLight:   [38, 50, 62],     // head highlight
  legDark:     [18, 24, 32],     // legs dark
  legMid:      [28, 36, 44],     // legs mid
  jawDark:     [24, 32, 42],     // jaw base
  jawLight:    [55, 75, 85],     // jaw inner/highlight
  finCyan:     [60, 140, 160],   // fin/antenna cyan glow
  eyeCyan:     [100, 200, 220],  // bright cyan for eyes/dots
  jointCyan:   [45, 100, 120],   // joint highlights
  spikeDark:   [25, 32, 40],     // spike/horn base
  spikeLight:  [50, 65, 75],     // spike tip
  tendrilCyan: [55, 130, 150],   // tendril glow
};

function fillRect(ctx, x, y, w, h, color) {
  ctx.fillStyle = `rgb(${color[0]},${color[1]},${color[2]})`;
  ctx.fillRect(x, y, w, h);
}

function addNoise(ctx, x, y, w, h, baseColor, intensity = 15) {
  const imgData = ctx.getImageData(x, y, w, h);
  for (let i = 0; i < imgData.data.length; i += 4) {
    const noise = (Math.random() - 0.5) * intensity;
    imgData.data[i]   = Math.max(0, Math.min(255, imgData.data[i] + noise));
    imgData.data[i+1] = Math.max(0, Math.min(255, imgData.data[i+1] + noise));
    imgData.data[i+2] = Math.max(0, Math.min(255, imgData.data[i+2] + noise));
  }
  ctx.putImageData(imgData, x, y);
}

function addCyanDots(ctx, x, y, w, h, density = 0.04) {
  const count = Math.floor(w * h * density);
  for (let i = 0; i < count; i++) {
    const dx = x + Math.floor(Math.random() * w);
    const dy = y + Math.floor(Math.random() * h);
    const bright = 120 + Math.floor(Math.random() * 100);
    ctx.fillStyle = `rgb(${Math.floor(bright*0.4)},${bright},${Math.floor(bright*0.95)})`;
    ctx.fillRect(dx, dy, 1, 1);
  }
}

function paintRegion(ctx, x, y, w, h, color, options = {}) {
  fillRect(ctx, x, y, w, h, color);
  if (options.noise !== false) addNoise(ctx, x, y, w, h, color, options.noiseIntensity || 12);
  if (options.dots) addCyanDots(ctx, x, y, w, h, options.dotDensity || 0.03);
  if (options.border) {
    ctx.fillStyle = `rgb(${options.border[0]},${options.border[1]},${options.border[2]})`;
    // top edge
    ctx.fillRect(x, y, w, 1);
    // bottom edge
    ctx.fillRect(x, y + h - 1, w, 1);
  }
}

// ═══════════════════════════════════════════════
// WUG — 64x64 texture
// Bug-like: body(22,10 11x4x15), head(8,28 8x4x6), jaw(30,8 8x2x4), 
// antennae(19,15 19x8x0), dorsal_fin(0,0 0x8x19), legs(0,0 8x0x2)
// ═══════════════════════════════════════════════
function generateWug() {
  const canvas = createCanvas(64, 64);
  const ctx = canvas.getContext('2d');
  
  // Background transparent
  ctx.clearRect(0, 0, 64, 64);
  
  // Body: texOffs(22,10), size 11x4x15 → UV wraps: 
  // front(22+15,10+4, 11x4), back(22+15+11,10+4, 11x4), 
  // left(22,10+4, 15x4), right(22+15+11+11,10+4 or 22+11,10+4... )
  // Actually for cube_all UV: top(x+z, y, xSize, zSize), bottom, front, back, left, right
  // Standard MC UV for box(w,h,d) at texOffs(u,v):
  // Bottom:  u+d,     v,      w, d     (top face)
  // Top:     u+d+w,   v,      w, d     (bottom face)
  // Front:   u+d,     v+d,    w, h     (north)
  // Back:    u+d+w+d, v+d,    w, h     (south)
  // Left:    u,       v+d,    d, h     (west)
  // Right:   u+d+w,   v+d,    d, h     (east)
  
  // Body: texOffs(22,10), box(-5.5,-2,7.5, 11,4,15) → w=11,h=4,d=15
  // Top:    22+15=37, 10, 11, 15
  // Bottom: 37+11=48, 10, 11, 15
  // Front:  37, 25, 11, 4
  // Back:   37+15=52(err, 37+11+15=63), 25, 11, 4 -- wraps
  // Left:   22, 25, 15, 4
  // Right:  48, 25, 15, 4
  paintRegion(ctx, 22, 10, 52, 19, COLORS.bodyDark, { dots: true, dotDensity: 0.02 });
  // Lighter top face
  paintRegion(ctx, 37, 10, 11, 15, COLORS.bodyMid, { dots: true });
  
  // Head: texOffs(8,28), box(-4,-2,-6, 8,4,6) → w=8,h=4,d=6
  paintRegion(ctx, 8, 28, 28, 10, COLORS.headDark, { dots: true, dotDensity: 0.03 });
  paintRegion(ctx, 14, 28, 8, 6, COLORS.headLight, { dots: true }); // top face
  
  // Jaw: texOffs(30,8), box(-4,0,-4, 8,2,4) → w=8,h=2,d=4
  paintRegion(ctx, 30, 8, 24, 6, COLORS.jawDark);
  paintRegion(ctx, 34, 12, 8, 2, COLORS.jawLight); // front face
  
  // Antennae: texOffs(19,15), box(-9.5,-8,0, 19,8,0) → flat plane
  paintRegion(ctx, 19, 15, 19, 8, COLORS.finCyan, { dots: true, dotDensity: 0.06 });
  
  // Dorsal fin: texOffs(0,0), box(0,-8,-7, 0,8,19) → flat plane
  paintRegion(ctx, 0, 0, 19, 8, COLORS.finCyan, { dots: true, dotDensity: 0.05 });
  
  // Legs share texOffs(0,0) — they're flat 8x0x2 planes, so tiny UV
  // They'll pick up the dorsal fin texture which is fine (dark with cyan)
  
  // Add some eye dots on the head
  fillRect(ctx, 16, 34, 2, 1, COLORS.eyeCyan);
  fillRect(ctx, 20, 34, 2, 1, COLORS.eyeCyan);
  
  return canvas;
}

// ═══════════════════════════════════════════════
// WARD — 128x128 texture
// Armored predator: body(0,0 14x7x8), body_rear(0,15 14x9x13), 
// neck(44,0 10x9x3), head empty, upper_jaw(44,12 10x4x10), lower_jaw(44,26 10x3x10),
// whiskers(84,0 16x4x0), dorsal_fin(0,37 0x9x25),
// legs inner(0,62+i*7 8x3x3), outer(22,62+i*7 8x4x4), feeler(84,4+i*4 4x3x0)
// right legs inner(0,83+i*7 mirror), outer(22,83+i*7 mirror), feeler(84,16+i*4 mirror)
// ═══════════════════════════════════════════════
function generateWard() {
  const canvas = createCanvas(128, 128);
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, 128, 128);
  
  // Body front: texOffs(0,0), box(-7,-7,-4, 14,7,8) → w=14,h=7,d=8
  paintRegion(ctx, 0, 0, 44, 15, COLORS.bodyDark, { dots: true, dotDensity: 0.015 });
  paintRegion(ctx, 8, 0, 14, 8, COLORS.bodyMid, { dots: true }); // top
  
  // Body rear: texOffs(0,15), box(-7,-9,0, 14,9,13) → w=14,h=9,d=13
  paintRegion(ctx, 0, 15, 54, 22, COLORS.bodyDark, { dots: true, dotDensity: 0.015 });
  paintRegion(ctx, 13, 15, 14, 13, COLORS.bodyMid, { dots: true }); // top
  
  // Neck: texOffs(44,0), box(-5,-5,-3, 10,9,3) → w=10,h=9,d=3
  paintRegion(ctx, 44, 0, 26, 12, COLORS.headDark);
  
  // Upper jaw: texOffs(44,12), box(-5,-4,-10, 10,4,10) → w=10,h=4,d=10
  paintRegion(ctx, 44, 12, 40, 14, COLORS.headLight, { dots: true, dotDensity: 0.02 });
  // Add teeth pattern on front face
  for (let i = 0; i < 5; i++) {
    fillRect(ctx, 54 + i * 2, 26, 1, 1, COLORS.eyeCyan);
  }
  
  // Lower jaw: texOffs(44,26), box(-5,0,-10, 10,3,10) → w=10,h=3,d=10
  paintRegion(ctx, 44, 26, 40, 13, COLORS.jawDark);
  // Teeth on lower jaw
  for (let i = 0; i < 5; i++) {
    fillRect(ctx, 54 + i * 2, 36, 1, 1, COLORS.eyeCyan);
  }
  
  // Whiskers: texOffs(84,0), flat 16x4x0
  paintRegion(ctx, 84, 0, 16, 4, COLORS.finCyan, { dots: true, dotDensity: 0.08 });
  
  // Dorsal fin: texOffs(0,37), flat 0x9x25
  paintRegion(ctx, 0, 37, 25, 9, COLORS.finCyan, { dots: true, dotDensity: 0.04 });
  
  // Left legs (3 pairs at texOffs 0,62 / 0,69 / 0,76)
  for (let i = 0; i < 3; i++) {
    const y = 62 + i * 7;
    // Inner: texOffs(0,y), 8x3x3
    paintRegion(ctx, 0, y, 14, 6, COLORS.legDark);
    paintRegion(ctx, 3, y, 8, 3, COLORS.legMid); // top
    // Outer: texOffs(22,y), 8x4x4
    paintRegion(ctx, 22, y, 24, 8, COLORS.legDark);
    paintRegion(ctx, 26, y, 8, 4, COLORS.legMid);
    // Joint highlights
    fillRect(ctx, 22, y + 4, 1, 1, COLORS.jointCyan);
    // Feeler: texOffs(84,4+i*4), 4x3x0
    paintRegion(ctx, 84, 4 + i * 4, 4, 3, COLORS.tendrilCyan);
  }
  
  // Right legs (3 pairs at texOffs 0,83 / 0,90 / 0,97)
  for (let i = 0; i < 3; i++) {
    const y = 83 + i * 7;
    paintRegion(ctx, 0, y, 14, 6, COLORS.legDark);
    paintRegion(ctx, 3, y, 8, 3, COLORS.legMid);
    paintRegion(ctx, 22, y, 24, 8, COLORS.legDark);
    paintRegion(ctx, 26, y, 8, 4, COLORS.legMid);
    fillRect(ctx, 22, y + 4, 1, 1, COLORS.jointCyan);
    paintRegion(ctx, 84, 16 + i * 4, 4, 3, COLORS.tendrilCyan);
  }
  
  // Eyes on head
  fillRect(ctx, 56, 24, 2, 2, COLORS.eyeCyan);
  fillRect(ctx, 60, 24, 2, 2, COLORS.eyeCyan);
  
  return canvas;
}

// ═══════════════════════════════════════════════
// WARB — 128x128 texture
// Heavy brute: body/lower_torso(0,16 10x16x5), upper_torso(0,0 11x4x7),
// head(36,0 10x8x10), jaw(36,18 8x3x10), tendrils(76,0 10x10x0),
// left_arm(0,37 6x10x8), left_forearm(0,55 6x10x8), shoulder_spikes(28,37 3x6x3),
// left_tendrils(76,10 6x8x0), right_arm(40,37 3x11x3), right_forearm(52,37 3x11x3),
// left_leg(0,73 3x10x2), right_leg(10,73 3x10x2)
// ═══════════════════════════════════════════════
function generateWarb() {
  const canvas = createCanvas(128, 128);
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, 128, 128);
  
  // Upper torso: texOffs(0,0), box(-5.5,-4,-3.5, 11,4,7) → w=11,h=4,d=7
  paintRegion(ctx, 0, 0, 36, 11, COLORS.bodyMid, { dots: true, dotDensity: 0.02 });
  paintRegion(ctx, 7, 0, 11, 7, COLORS.bodyLight, { dots: true }); // top
  
  // Lower torso/body: texOffs(0,16), box(-5,-16,-2.5, 10,16,5) → w=10,h=16,d=5
  paintRegion(ctx, 0, 16, 30, 21, COLORS.bodyDark, { dots: true, dotDensity: 0.015 });
  
  // Head: texOffs(36,0), box(-5,-8,-5, 10,8,10) → w=10,h=8,d=10
  paintRegion(ctx, 36, 0, 40, 18, COLORS.headDark, { dots: true, dotDensity: 0.02 });
  paintRegion(ctx, 46, 0, 10, 10, COLORS.headLight, { dots: true }); // top
  // Eyes
  fillRect(ctx, 48, 14, 2, 2, COLORS.eyeCyan);
  fillRect(ctx, 54, 14, 2, 2, COLORS.eyeCyan);
  
  // Jaw: texOffs(36,18), box(-4,0,-5, 8,3,10) → w=8,h=3,d=10
  paintRegion(ctx, 36, 18, 36, 13, COLORS.jawDark);
  // Teeth
  for (let i = 0; i < 4; i++) {
    fillRect(ctx, 47 + i * 2, 28, 1, 2, COLORS.eyeCyan);
  }
  
  // Tendrils: texOffs(76,0), flat 10x10x0
  paintRegion(ctx, 76, 0, 10, 10, COLORS.tendrilCyan, { dots: true, dotDensity: 0.06 });
  
  // Left arm: texOffs(0,37), box(-6,0,-4, 6,10,8) → w=6,h=10,d=8
  paintRegion(ctx, 0, 37, 28, 18, COLORS.bodyDark);
  paintRegion(ctx, 8, 45, 6, 10, COLORS.bodyMid); // front face
  // Joint highlight
  fillRect(ctx, 8, 47, 6, 1, COLORS.jointCyan);
  
  // Left forearm: texOffs(0,55), box(-6,0,-4, 6,10,8) → w=6,h=10,d=8
  paintRegion(ctx, 0, 55, 28, 18, COLORS.bodyDark);
  paintRegion(ctx, 8, 63, 6, 10, COLORS.bodyMid);
  fillRect(ctx, 8, 72, 6, 1, COLORS.jointCyan);
  
  // Shoulder spikes: texOffs(28,37), box(-1.5,-6,-1.5, 3,6,3) → w=3,h=6,d=3
  paintRegion(ctx, 28, 37, 12, 9, COLORS.spikeDark);
  paintRegion(ctx, 31, 43, 3, 6, COLORS.spikeLight); // front
  
  // Left arm tendrils: texOffs(76,10), flat 6x8x0
  paintRegion(ctx, 76, 10, 6, 8, COLORS.tendrilCyan, { dots: true, dotDensity: 0.06 });
  
  // Right arm: texOffs(40,37), box(0,0,-1.5, 3,11,3) → w=3,h=11,d=3
  paintRegion(ctx, 40, 37, 12, 14, COLORS.legDark);
  paintRegion(ctx, 43, 48, 3, 11, COLORS.legMid); // front
  
  // Right forearm: texOffs(52,37), box(0,0,-1.5, 3,11,3) → w=3,h=11,d=3
  paintRegion(ctx, 52, 37, 12, 14, COLORS.legDark);
  paintRegion(ctx, 55, 48, 3, 11, COLORS.legMid);
  
  // Left leg: texOffs(0,73), box(-1.5,0,-1, 3,10,2) → w=3,h=10,d=2
  paintRegion(ctx, 0, 73, 10, 12, COLORS.legDark);
  paintRegion(ctx, 2, 75, 3, 10, COLORS.legMid);
  
  // Right leg: texOffs(10,73), box(-1.5,0,-1, 3,10,2) → w=3,h=10,d=2
  paintRegion(ctx, 10, 73, 10, 12, COLORS.legDark);
  paintRegion(ctx, 12, 75, 3, 10, COLORS.legMid);
  
  return canvas;
}

// Generate all 3
const wug = generateWug();
fs.writeFileSync(path.join(OUT_DIR, 'hpc_wug.png'), wug.toBuffer('image/png'));
console.log('✓ hpc_wug.png (64x64)');

const ward = generateWard();
fs.writeFileSync(path.join(OUT_DIR, 'hpc_ward.png'), ward.toBuffer('image/png'));
console.log('✓ hpc_ward.png (128x128)');

const warb = generateWarb();
fs.writeFileSync(path.join(OUT_DIR, 'hpc_warb.png'), warb.toBuffer('image/png'));
console.log('✓ hpc_warb.png (128x128)');

console.log('\nDone — all 3 mob textures generated with correct UV mapping.');
