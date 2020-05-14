from PIL import Image
import os
import sys

def enlarge(filename, savename):
    im = Image.open(filename)
    pixels = im.load()
    newim = Image.new('RGBA', (im.size[0] * 2, im.size[1] * 2))
    np = newim.load()
    for i in range(newim.size[0]):
        for j in range(newim.size[1]):
            np[i, j] = pixels[i//2, j//2]
    newim.save(savename)
    
def shrink(filename, savename):
    im = Image.open(filename)
    pixels = im.load()
    newim = Image.new('RGBA', (im.size[0] // 2, im.size[1] // 2))
    np = newim.load()
    for i in range(im.size[0]):
        for j in range(im.size[1]):
            np[i//2, j//2] = pixels[i, j]
    newim.save(savename)

def mass_convert(func, srcdir, enddir):
    for name in os.listdir(srcdir):
        srcname = srcdir + '/' + name
        endname = enddir + '/' + name
        if os.path.isdir(srcname):
            os.mkdir(endname)
            mass_convert(func, srcname, endname)
        else:
            #print(srcname)
            func(srcname, endname)

def do_convert():
    mass_convert(enlarge, '16px-src', '32px-der')
    mass_convert(shrink, '32px-src', '16px-der')

if __name__ == '__main__':
    if (len(sys.argv) > 1):
        os.chdir(sys.argv[1])
    #print(os.getcwd())
    do_convert()
