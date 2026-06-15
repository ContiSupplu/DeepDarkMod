#!/usr/bin/env python3
"""
echo_recolor.py - vanilla Minecraft block textures -> Echo Dimension blocks.

PALETTE COMES FROM SCULK ITSELF. The script samples vanilla sculk.png's actual colors
(dark blue-black -> teal -> cyan) and maps EVERY block onto that palette, so the whole set
is one sculk-colored family. Structural blocks (dirt/stone/gravel/wood) map onto the DARK
part only (dark sculk-stone look, no cyan dots). The ground (sculk_grass) keeps sculk's
full range incl. the bright cyan dots, and its animation (.mcmeta) is carried over so it
shimmers like sculk. The grass SIDE uses vanilla's grass_block_side_overlay for a natural
organic edge (not a straight band).

INPUTS (from 1.21.1 client jar, assets/minecraft/textures/block/):
  sculk.png (+ sculk.png.mcmeta), grass_block_side.png, grass_block_side_overlay.png,
  dirt.png, stone.png, gravel.png, oak_leaves.png, oak_log.png, oak_log_top.png,
  oak_planks.png, stripped_oak_log(_top).png, oak_door_top/bottom.png, oak_trapdoor.png
RUN:
  python echo_recolor.py --in <vanilla_dir> --out <echo_dir> --emissive
Copy outputs (PNG + .mcmeta) into assets/otherside/textures/block/.
NOTE: outputs are recolors of vanilla textures (standard for mods); distribution is your call.
"""
import argparse, os, shutil, json, numpy as np
from PIL import Image

# fallback sculk palette if sculk.png not supplied (approx vanilla sculk)
FALLBACK = [(9,12,18),(16,26,33),(24,43,47),(38,74,73),(55,115,108),(82,170,156),(116,214,196)]
# structural blocks only use the lower part of the palette (no bright cyan dots)
STRUCT_LMAX = 0.55

def luminance(a): return (0.299*a[...,0]+0.587*a[...,1]+0.114*a[...,2])/255.0

def sample_sculk_palette(sculk_img, n=7):
    a=np.asarray(sculk_img.convert('RGB'))[:16,:16].reshape(-1,3).astype(float)
    L=luminance(a); stops=[]
    for k in range(n):
        t=k/(n-1); idx=np.argsort(np.abs(L-t))[:max(4,len(L)//n)]
        stops.append(tuple(np.median(a[idx],axis=0)))
    return stops

def build_lut(stops, lmax=1.0):
    lut=np.zeros((256,3))
    for i in range(256):
        L=(i/255.0)*lmax; seg=L*(len(stops)-1); j=min(int(seg),len(stops)-2); f=seg-j
        a,b=np.array(stops[j]),np.array(stops[j+1]); lut[i]=a+(b-a)*f
    return lut

def recolor(img, stops, lmax=1.0):
    img=img.convert('RGBA'); a=np.asarray(img); rgb=a[...,:3].astype(float); al=a[...,3]
    L=luminance(rgb); lut=build_lut(stops,lmax)
    idx=np.clip((L*255).astype(int),0,255)
    out=lut[idx.reshape(-1)].reshape(L.shape+(3,))
    return Image.fromarray(np.dstack([np.clip(out,0,255).astype('uint8'),al]),'RGBA')

def emissive_from(img, stops, thresh=0.45):
    """keep only bright (dot) pixels, recolored; black elsewhere -> glow map."""
    img=img.convert('RGBA'); a=np.asarray(img); rgb=a[...,:3].astype(float); al=a[...,3]
    L=luminance(rgb); lut=build_lut(stops,1.0); idx=np.clip((L*255).astype(int),0,255)
    col=lut[idx.reshape(-1)].reshape(L.shape+(3,))
    mask=(L>thresh)[...,None]
    out=np.where(mask,col,0)
    return Image.fromarray(np.dstack([np.clip(out,0,255).astype('uint8'),al]),'RGBA')

def copy_mcmeta(src,out):
    if os.path.exists(src+'.mcmeta'): shutil.copy(src+'.mcmeta', out+'.mcmeta'); return True
    return False

# vanilla -> (echo out, 'struct'|'ground'|'overlay-handled')
STRUCT = {
    'dirt.png':('sculk_dirt.png'), 'stone.png':('sculk_stone.png'), 'gravel.png':('sculk_gravel.png'),
    'oak_leaves.png':('sculk_leaves.png'), 'oak_log.png':('echo_log.png'),
    'oak_log_top.png':('echo_log_top.png'), 'oak_planks.png':('echo_planks.png'),
    'stripped_oak_log.png':('stripped_echo_log.png'), 'stripped_oak_log_top.png':('stripped_echo_log_top.png'),
    'oak_door_top.png':('echo_door_top.png'), 'oak_door_bottom.png':('echo_door_bottom.png'),
    'oak_trapdoor.png':('echo_trapdoor.png'),
}

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument('--in',dest='indir',required=True); ap.add_argument('--out',dest='outdir',required=True)
    ap.add_argument('--emissive',action='store_true'); a=ap.parse_args()
    os.makedirs(a.outdir,exist_ok=True)
    sp=os.path.join(a.indir,'sculk.png')
    stops = sample_sculk_palette(Image.open(sp)) if os.path.exists(sp) else FALLBACK
    print('palette source:', 'sculk.png' if os.path.exists(sp) else 'FALLBACK')

    # GROUND: sculk_grass_top = sculk recolored onto its own palette (~identity) + carry animation
    if os.path.exists(sp):
        o=os.path.join(a.outdir,'sculk_grass_top.png')
        recolor(Image.open(sp),stops,1.0).save(o); anim=copy_mcmeta(sp,o)
        if a.emissive:
            oe=o.replace('.png','_e.png'); emissive_from(Image.open(sp),stops).save(oe)
            if anim: copy_mcmeta(sp,oe)

    # STRUCTURAL: dark sculk palette (lower range), no cyan dots
    done=0
    for src,dst in STRUCT.items():
        p=os.path.join(a.indir,src)
        if not os.path.exists(p): continue
        recolor(Image.open(p),stops,STRUCT_LMAX).save(os.path.join(a.outdir,dst)); done+=1
        if src=='dirt.png':
            recolor(Image.open(p),stops,STRUCT_LMAX).save(os.path.join(a.outdir,'sculk_grass_bottom.png'))

    # GRASS SIDE: dark sculk dirt + recolored vanilla overlay (natural organic edge)
    base_p=os.path.join(a.indir,'grass_block_side.png'); ov_p=os.path.join(a.indir,'grass_block_side_overlay.png')
    if os.path.exists(base_p) and os.path.exists(ov_p):
        base=recolor(Image.open(base_p),stops,STRUCT_LMAX).convert('RGBA')
        overlay=recolor(Image.open(ov_p),stops,1.0).convert('RGBA')  # full range -> cyan fringe
        ov=np.asarray(Image.open(ov_p).convert('RGBA')); ov_alpha=ov[...,3]      # keep vanilla's organic shape
        b=np.asarray(base).copy(); o=np.asarray(overlay)
        m=ov_alpha>10; b[m]=o[m]
        Image.fromarray(b,'RGBA').save(os.path.join(a.outdir,'sculk_grass_side.png'))
    elif os.path.exists(os.path.join(a.indir,'dirt.png')):
        # fallback side: dirt + a few cyan specks near top
        side=np.asarray(recolor(Image.open(os.path.join(a.indir,'dirt.png')),stops,STRUCT_LMAX)).copy()
        lut=build_lut(stops,1.0)
        rng=np.random.default_rng(1)
        for x in range(16):
            for y in range(rng.integers(2,6)):
                if rng.random()<0.7: side[y,x,:3]=lut[200]
        Image.fromarray(side,'RGBA').save(os.path.join(a.outdir,'sculk_grass_side.png'))

    print(f'recolored ground + {done} structural blocks + grass side -> {a.outdir}')

if __name__=='__main__': main()
