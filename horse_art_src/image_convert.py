from PIL import Image
import os

def color_to_white(filename):
    im = Image.open(filename)
    pixels = im.load()
    for i in range(im.size[0]):
        for j in range(im.size[1]):
            a = pixels[i, j][3]
            if a != 0:
                pixels[i, j] = (255, 255, 255, a)
    im.save(filename)
    
def color_to_reference(filename, savename, refname='white_32px.png'):
    im = Image.open(filename)
    ref = Image.open(refname)
    pixels = im.load()
    rpix = ref.load()
    for i in range(im.size[0]):
        for j in range(im.size[1]):
            a = pixels[i, j][3]
            rp = rpix[i, j]
            if a != 0:
                #print(rp)
                pixels[i, j] = (rp[0], rp[1], rp[2], a)
    im.save(savename)
    #im.show()
    

# For when the back face of the mane, the one that's only visible while
# the horse is grazing, is the wrong color
def fix_mane(filename):
    im = Image.open(filename)
    pixels = im.load()
    pixels[64, 3] = pixels[63, 19]
    pixels[64, 2] = pixels[61, 19]
    pixels[64, 1] = pixels[69, 19]
    pixels[64, 0] = pixels[59, 19]
    pixels[65, 3] = pixels[62, 19]
    pixels[65, 2] = pixels[58, 19]
    pixels[65, 1] = pixels[68, 19]
    pixels[65, 0] = pixels[67, 19]
    #im.show()
    im.save(filename)

def fix_hooves(srcname, endname):
    im = Image.open(srcname)
    pixels = im.load()
    for x in [48, 64, 82, 100]:
        for i in range(3):
            for j in range(3):
                pixels[i + x - 1, j + 51] = pixels[i + x, j + 51]
        for i in range(3):
            for j in range(3):
                pixels[i + x + 2, j + 51] = pixels[i + x + 3, j + 51]
        for i in range(16):
            for j in range(3):
                pixels[i + x - 4, j + 54] = pixels[i + x - 4, j + 55]
            pixels[i + x - 4, 57] = 0
    im.save(endname)            

def mass_convert(func, srcdir, enddir):
    if not os.path.isdir(srcdir):
        print 'skipping ' + srcdir
        return
    create_directory(enddir)
    if not os.path.isdir(enddir):
        os.mkdir(enddir)
    for name in os.listdir(srcdir):
        srcname = srcdir + '/' + name
        endname = enddir + '/' + name
        if os.path.isdir(srcname):
            os.mkdir(endname)
            mass_convert(func, srcname, endname)
        else:
            #print(srcname)
            func(srcname, endname)

def create_directory(dirname):
    if os.path.isdir(dirname):
        return
    folder = os.path.dirname(dirname)
    print folder
    if folder:
        create_directory(folder)
    os.mkdir(dirname)

    
if False:
    import os
    for filename in os.listdir("horse/socks"):
        color_to_reference("horse/socks/" + filename, 'white.png')
    for filename in os.listdir("horse/pinto"):
        color_to_reference("horse/pinto/" + filename, 'white.png')
    for filename in os.listdir("horse/face"):
        color_to_reference("horse/face/" + filename, 'white.png')

if False:
    import os
    folder = "faces32px/"
    for filename in os.listdir(folder):
        color_to_reference(folder + filename, "white_32px.png")

if False:
    path = '/entity/horse'
    mass_convert(fix_hooves, "16px-src_big_hooves" + path, "16px-src" + path)
    mass_convert(fix_hooves, "16px-unq_big_hooves" + path, "16px-unq" + path)

if True:
    base='/entity/horse/'
    folders = ['pinto', 'face', 'socks', 'leopard', 'roan']
    for folder in folders:
        path = base + folder
        mass_convert(color_to_reference, '32px-src_nostrils' + path, '32px-src' + path)
        mass_convert(color_to_reference, '32px-unq_nostrils' + path, '32px-unq' + path)
