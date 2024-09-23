from PIL import Image
import os
import sys

def mkdir(dirname):
    if not os.path.isdir(dirname):
        os.mkdir(dirname)

def enlarge(filename, savename):
    im = Image.open(filename)
    pixels = im.load()
    newim = Image.new('RGBA', (im.size[0] * 2, im.size[1] * 2))
    np = newim.load()
    for i in range(newim.size[0]):
        for j in range(newim.size[1]):
            np[i, j] = pixels[i//2, j//2]
    newim.save(savename)

# 'mirror' is the areas that should be mirrored on the model but aren't
# To preserve symmetry, we take the pixel from the other side in those areas
# Why not just set them as mirrored in the model? To preserve compatability
# with other mods expecting the horse model to be unchanged
# mirror should be a list of dictionaries that each have the entries 'x', 'y',
# 'w', and 'h', x, y = top left corner, w, h = width and height
# mirror_width should be the width of the texture that is used to define the
# mirror boxes (e.g. the width of the entity's texture in 16px resolution)
def shrink(filename, savename, mirror=[], mirror_width=128):
    im = Image.open(filename)
    pixels = im.load()
    newim = Image.new('RGBA', (im.size[0] // 2, im.size[1] // 2))
    np = newim.load()
    mirror_scale = im.size[0] // mirror_width
    for i in range(0, im.size[0], 2):
        for j in range(0, im.size[1], 2):
            is_mirrored = 0
            for box in mirror:
                if i >= box['x'] * mirror_scale \
                    and i < (box['x'] + box['w']) * mirror_scale \
                    and j >= box['y'] * mirror_scale \
                    and j < (box['y'] + box['h']) * mirror_scale:
                        is_mirrored = 1
            np[i//2, j//2] = pixels[i + is_mirrored, j]
    newim.save(savename)

def mass_convert(func, srcdir, enddir):
    mkdir(enddir)
    for name in os.listdir(srcdir):
        srcname = srcdir + '/' + name
        endname = enddir + '/' + name
        if os.path.isdir(srcname):
            mass_convert(func, srcname, endname)
        else:
            func(srcname, endname)

def do_convert():
    horse_mirror = [
        {'x':60, 'y': 33, 'w': 14, 'h':8}, # Right front upper leg
        {'x':60, 'y': 44, 'w': 12, 'h':5}, # Right front lower leg
        {'x':60, 'y': 55, 'w': 16, 'h':3}, # Right front hoof
        {'x':96, 'y': 34, 'w': 18, 'h':9}, # Right back upper leg
        {'x':96, 'y': 46, 'w': 12, 'h':5}, # Right back lower leg
        {'x':96, 'y': 55, 'w': 16, 'h':3}  # Right back hoof
        ]
    if os.path.isdir('16px-src'):
        mass_convert(enlarge, '16px-src', '32px-der')
    if os.path.isdir('32px-src'):
        mass_convert(lambda x,y: shrink(x, y, mirror=horse_mirror), '32px-src', '16px-der')

if __name__ == '__main__':
    if (len(sys.argv) > 1):
        os.chdir(sys.argv[1])
    #print(os.getcwd())
    do_convert()
