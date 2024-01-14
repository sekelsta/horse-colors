def get_gene(g, i):
    return g[2 * i:2 * i + 2]

def get_readable_genes(g):
    r = {}
    r['KIT'] = get_gene(g, 15)
    r['MITF'] = get_gene(g, 16)
    r['white_star'] = get_gene(g, 25)
    r['white_forelegs'] = get_gene(g, 26)
    r['white_hindlegs'] = get_gene(g, 27)
    r['PAX3'] = get_gene(g, 49)
    r['white_suppression'] = get_gene(g, 50)
    return r
