/**
 * Generate seamless grayscale cloud textures for the echo portal effect.
 * - echo_portal_clouds_far.png  (64x64, large soft clouds)
 * - echo_portal_clouds_near.png (64x64, finer detail clouds)
 */
const fs = require('fs');
const path = require('path');
const { createCanvas } = require('@napi-rs/canvas');

const OUT_DIR = path.join(__dirname, 'src', 'main', 'resources', 'assets', 'otherside', 'textures', 'effect');

// Seeded random for reproducibility
function mulberry32(a) {
  return function() {
    a |= 0; a = a + 0x6D2B79F5 | 0;
    var t = Math.imul(a ^ a >>> 15, 1 | a);
    t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t;
    return ((t ^ t >>> 14) >>> 0) / 4294967296;
  }
}

// Simple 2D noise using interpolation of random grid
function makeNoise(size, gridSize, seed) {
  const rand = mulberry32(seed);
  const grid = [];
  const gs = gridSize + 1;
  for (let i = 0; i < gs * gs; i++) grid.push(rand());
  
  const result = new Float32Array(size * size);
  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      const gx = (x / size) * gridSize;
      const gy = (y / size) * gridSize;
      const ix = Math.floor(gx) % gridSize;
      const iy = Math.floor(gy) % gridSize;
      const fx = gx - Math.floor(gx);
      const fy = gy - Math.floor(gy);
      // Smoothstep
      const sx = fx * fx * (3 - 2 * fx);
      const sy = fy * fy * (3 - 2 * fy);
      // Bilinear with wrapping
      const g = (gx2, gy2) => grid[(gy2 % gridSize) * gs + (gx2 % gridSize)];
      const v00 = g(ix, iy);
      const v10 = g(ix + 1, iy);
      const v01 = g(ix, iy + 1);
      const v11 = g(ix + 1, iy + 1);
      const top = v00 + (v10 - v00) * sx;
      const bot = v01 + (v11 - v01) * sx;
      result[y * size + x] = top + (bot - top) * sy;
    }
  }
  return result;
}

function generateCloudTexture(size, octaves, persistence, seedBase) {
  const result = new Float32Array(size * size);
  let amplitude = 1.0;
  let totalAmplitude = 0;
  
  for (let oct = 0; oct < octaves; oct++) {
    const gridSize = Math.max(2, Math.pow(2, oct + 1));
    const noise = makeNoise(size, gridSize, seedBase + oct * 1000);
    for (let i = 0; i < size * size; i++) {
      result[i] += noise[i] * amplitude;
    }
    totalAmplitude += amplitude;
    amplitude *= persistence;
  }
  
  // Normalize to 0-1
  let min = Infinity, max = -Infinity;
  for (let i = 0; i < result.length; i++) {
    result[i] /= totalAmplitude;
    if (result[i] < min) min = result[i];
    if (result[i] > max) max = result[i];
  }
  const range = max - min || 1;
  for (let i = 0; i < result.length; i++) {
    result[i] = (result[i] - min) / range;
  }
  
  return result;
}

function saveGrayscale(data, size, filePath) {
  const canvas = createCanvas(size, size);
  const ctx = canvas.getContext('2d');
  const imgData = ctx.createImageData(size, size);
  for (let i = 0; i < size * size; i++) {
    const v = Math.round(data[i] * 255);
    imgData.data[i * 4]     = v;
    imgData.data[i * 4 + 1] = v;
    imgData.data[i * 4 + 2] = v;
    imgData.data[i * 4 + 3] = 255;
  }
  ctx.putImageData(imgData, 0, 0);
  fs.writeFileSync(filePath, canvas.toBuffer('image/png'));
}

// Generate
fs.mkdirSync(OUT_DIR, { recursive: true });

// Far clouds: large soft shapes (few octaves, low grid sizes)
const farClouds = generateCloudTexture(64, 3, 0.5, 42);
saveGrayscale(farClouds, 64, path.join(OUT_DIR, 'echo_portal_clouds_far.png'));
console.log('✓ echo_portal_clouds_far.png (64x64)');

// Near clouds: finer detail (more octaves, higher grid sizes)
const nearClouds = generateCloudTexture(64, 5, 0.6, 137);
saveGrayscale(nearClouds, 64, path.join(OUT_DIR, 'echo_portal_clouds_near.png'));
console.log('✓ echo_portal_clouds_near.png (64x64)');

console.log('Cloud textures saved to', OUT_DIR);
