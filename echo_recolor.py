#!/usr/bin/env python3
"""
echo_recolor.py - turn vanilla Minecraft block textures into Echo Dimension blocks.

PRINCIPLE
  Keep the vanilla DESIGN (the pixel layout / shading that makes a block readable and
  tileable); swap only the PALETTE. Each texture -> luminance -> mapped onto a muted
  echo ramp. One cool grey-blue family so everything blends.

  GLOW LIVES IN THE GROUND. Only the ground surface (sculk_grass) carries the cyan-blue
  glowing dots - it is recolored from vanilla SCULK, which already has the dot pattern.
  Dirt / stone / gravel / wood / planks stay dark and glowless. (Glowing every block =
  cyberpunk; glowing only the ground = sculk world.)

RUN
  1. Copy these vanilla 1.21.1 textures from the client jar
     (assets/minecraft/textures/block/): sculk.png, dirt.png, stone.png (or deepslate.png),
     gravel.png, oak_leaves.png, oak_log.png, oak_log_top.png, oak_planks.png,
     stripped_oak_log(_top).png, oak_door_top/bottom.png, oak_trapdoor.png
  2. python echo_recolor.py --in <vanilla_dir> --out <echo_dir> [--emissive]
  3. Copy outputs into assets/otherside/textures/block/
  --emissive writes *_e.png glow-only maps for the dotted ground blocks (for bloom/
  emissive rendering). Ramps are tunable at the top.

NOTE: outputs are recolors of vanilla textures (standard for mods); distribution is your call.
"""
import argparse, os, numpy as np
from PIL import Image, ImageFilter

# shadow, mid, highlight  (sampled from the reference shots: muted, cool, low-sat)
RAMPS = {
    'sculk':  [(14,20,28), (30,40,50), (150,225,240)],  # dark base, BRIGHT CYAN dots -> the glowing ground
    'stone':  [(24,28,34), (51,57,65), (102,113,126)],
    'dirt':   [(26,26,30), (46,46,52), (86,86,94)],
    'wood':   [(30,29,33), (55,54,60), (99,100,108)],
    'wood_stripped': [(38,38,42),(66,66,72),(112,114,122)],
    'leaves': [(20,30,31), (44,60,60), (104,150,146)],
}
# vanilla file -> (echo output, ramp).  sculk.png is the GROUND source (has the dots).
MAPPING = {
    'sculk.png':                ('sculk_grass_top.png',      'sculk'),
    'dirt.png':                 ('sculk_dirt.png',           'dirt'),
    'stone.png':                ('sculk_stone.png',          'stone'),
    'gravel.png':               ('sculk_gravel.png',         'stone'),
    'oak_leaves.png':           ('sculk_leaves.png',         'leaves'),
    'oak_log.png':              ('echo_log.png',             'wood'),
    'oak_log_top.png':          ('echo_log_top.png',         'wood'),
    'oak_planks.png':           ('echo_planks.png',          'wood'),
    'stripped_oak_log.png':     ('stripped_echo_log.png',    'wood_stripped'),
    'stripped_oak_log_top.png': ('stripped_echo_log_top.png','wood_stripped'),
    'oak_door_top.png':         ('echo_door_top.png',        'wood'),
    'oak_door_bottom.png':      ('echo_door_bottom.png',     'wood'),
    'oak_trapdoor.png':         ('echo_trapdoor.png',        'wood'),
}
GLOW_RAMPS = {'sculk'}  # which ramps produce an emissive companion

def _lerp(a,b,t): return tuple(int(a[i]+(b[i]-a[i])*t) for i in range(3))
def _gmap(L,ramp):
    s,m,h=ramp
    return _lerp(s,m,L/0.5) if L<0.5 else _lerp(m,h,(L-0.5)/0.5)

def echo_recolor(img, ramp_key):
    img=img.convert('RGBA'); a=np.asarray(img)
    rgb=a[...,:3].astype(float); alpha=a[...,3]
    L=(0.299*rgb[...,0]+0.587*rgb[...,1]+0.114*rgb[...,2])/255.0
    lut=[_gmap(i/255.0,RAMPS[ramp_key]) for i in range(256)]
    idx=np.clip((L*255).astype(int),0,255)
    mapped=np.array([lut[i] for i in idx.reshape(-1)]).reshape(L.shape+(3,))
    out=np.dstack([mapped.astype(np.uint8),alpha])
    emissive=None
    if ramp_key in GLOW_RAMPS:
        # glow-only: keep only the bright (dot) pixels, black elsewhere
        em=np.zeros_like(out); g=(L>0.55)
        em[...,:3]=np.where(g[...,None], mapped.astype(np.uint8), 0); em[...,3]=alpha
        emissive=Image.fromarray(em,'RGBA')
    return Image.fromarray(out,'RGBA'), emissive

def make_sculk_ground(seed=5):
    """Procedural fallback if vanilla sculk.png isn't supplied. 16x16, seamless."""
    rng=np.random.default_rng(seed); S=16
    def wrap(sig):
        b=rng.random((S,S)); t=np.tile(b,(3,3))
        im=Image.fromarray((t*255).astype('uint8')).filter(ImageFilter.GaussianBlur(sig))
        x=np.asarray(im,float)[S:2*S,S:2*S]; return (x-x.min())/(x.max()-x.min()+1e-9)
    base=np.zeros((S,S,3)); bn=wrap(1.3)
    lo,hi=np.array([16,22,30]),np.array([30,40,52])
    for i in range(3): base[...,i]=lo[i]+(hi[i]-lo[i])*bn
    dots=np.zeros((S,S))
    for _ in range(int(S*S*0.12)):
        y,x=rng.integers(0,S),rng.integers(0,S); dots[y,x]=rng.uniform(0.5,1)
    dm=Image.fromarray((np.tile(dots,(3,3))*255).astype('uint8')).filter(ImageFilter.GaussianBlur(0.7))
    halo=np.asarray(dm,float)[S:2*S,S:2*S]/255.0
    glow=np.clip(halo*0.7+dots,0,1)
    dim,br=np.array([40,95,115]),np.array([150,225,240]); out=base.copy()
    for i in range(3):
        out[...,i]=np.where(glow>0.04, base[...,i]+((dim[i]+(br[i]-dim[i])*glow)-base[...,i])*np.clip(glow*1.4,0,1), base[...,i])
    rgb=Image.fromarray(np.clip(out,0,255).astype('uint8'),'RGB').convert('RGBA')
    em=np.zeros((S,S,3))
    for i in range(3): em[...,i]=(dim[i]+(br[i]-dim[i])*glow)*(glow>0.04)
    return rgb, Image.fromarray(np.clip(em,0,255).astype('uint8'),'RGB').convert('RGBA')

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument('--in',dest='indir',required=True)
    ap.add_argument('--out',dest='outdir',required=True)
    ap.add_argument('--emissive',action='store_true')
    a=ap.parse_args(); os.makedirs(a.outdir,exist_ok=True)
    done,miss=0,[]
    for src,(dst,ramp) in MAPPING.items():
        p=os.path.join(a.indir,src)
        if not os.path.exists(p): miss.append(src); continue
        base,em=echo_recolor(Image.open(p),ramp)
        base.save(os.path.join(a.outdir,dst)); done+=1
        if a.emissive and em is not None:
            em.save(os.path.join(a.outdir,dst.replace('.png','_e.png')))
        if src=='dirt.png':
            echo_recolor(Image.open(p),'dirt')[0].save(os.path.join(a.outdir,'sculk_grass_bottom.png'))
    # sculk_grass_side = echo dirt with a sculk-dot band creeping over the top edge
    if 'sculk.png' in [s for s in MAPPING] and os.path.exists(os.path.join(a.indir,'sculk.png')) \
       and os.path.exists(os.path.join(a.indir,'dirt.png')):
        side=np.asarray(echo_recolor(Image.open(os.path.join(a.indir,'dirt.png')),'dirt')[0]).copy()
        topband,_=echo_recolor(Image.open(os.path.join(a.indir,'sculk.png')),'sculk')
        tb=np.asarray(topband)
        side[:5,:,:]=tb[:5,:,:]  # top 5px = sculk band
        Image.fromarray(side,'RGBA').save(os.path.join(a.outdir,'sculk_grass_side.png'))
    if not os.path.exists(os.path.join(a.indir,'sculk.png')):
        rgb,em=make_sculk_ground(); rgb.save(os.path.join(a.outdir,'sculk_grass_top.png'))
        if a.emissive: em.save(os.path.join(a.outdir,'sculk_grass_top_e.png'))
        print('(no vanilla sculk.png -> used procedural sculk-ground fallback)')
    print(f'recolored {done} textures -> {a.outdir}')
    if miss: print('missing vanilla inputs (skipped):',', '.join(miss))

if __name__=='__main__': main()
