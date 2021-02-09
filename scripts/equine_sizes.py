'''Defines the size genes'''
from functools import reduce
import genetics

# Define size calculation functions

# For calculating perfectly incomplete dominant sizes
# The input is a list of how much each allele affects the size
def size_func(lst):
    return (lambda genes: reduce((lambda x, y: x * lst[y]), genes, 1.0))

# For calculating sizes where one allele has a greater effect than the other,
# and there are two alleles. The inputs are the effects on size from
# (wildtype, wildtype), (wildtype, mutant), and (mutant, mutant)
def codom_size_func(wild, hetero, homo):
    def f(genes):
        if all(x == 1 for x in genes):
            return homo
        elif all(x != 1 for x in genes):
            return wild
        else:
            return hetero
    return f

# For semi-recessive genes with more than 2 alleles
def semirec_size_func(lst):
    def f(genes):
        g0 = lst[genes[0]]
        g1 = lst[genes[1]]
        mn = min(g0, g1)
        mx = max(g0, g1)
        return mn ** 0.4 * mx ** 1.6
    return f

# Define the size genes and alleles
        
# All incomplete dominant
minor_sizes = [[1, 1.002, 1/1.002, 1.009, 1/1.009],
         [1, 1.003, 1/1.003, 1.015, 1/1.015],
         [1, 1.001, 1/1.001, 1.012, 1/1.012],
         [1, 1.001, 1/1.001, 1.01, 1/1.01],
         [1, 1.002, 1/1.002, 1.008, 1/1.008],
         [1, 1.001, 1/1.001, 1.005, 1/1.005],
         [1, 1.0025, 1/1.0025, 1.005, 1/1.005],
         [1, 1.0025, 1/1.0025, 1.005, 1/1.005]
         ]

subtle_alleles = [1, 1.001, 1/1.001, 1.002, 1/1.002,
                  1.003, 1/1.003, 1.004, 1/1.004]

sizes = {
    "LCORL":size_func([1, 1.03]),
    "HMGA2":codom_size_func(1, 0.94, 0.81),
    "size0":lambda genes: 1.06 if genes[0] == 1 else 1,
    "size1":codom_size_func(1, 1.08, 1.1),
    "size2":size_func([1, 1.002, 1.03, 1.05]),
    "size3":lambda genes: 1/1.08 if genes[1] == 1 else 1,
    "size4":semirec_size_func([1, 1/1.005, 1/1.02, 1/1.05, 1/1.06]),
    "donkey_size0":size_func([1, 1.01, 1.03]),
    "donkey_size1":size_func([1, 1.02, 1.04]),
    "donkey_size2":size_func([1, 1/1.02, 1/1.04]),
    "donkey_size3":size_func([1, 1/1.06]),
    "donkey_size4":codom_size_func(1, 1/1.02, 1/1.1),
    "donkey_size5":size_func([1, 1.025]),
    "donkey_size6":codom_size_func(1, 1.04, 1.06),
    # Hard-coded as if horses always have 0 and donkeys always have 1
    "donkey_extra":codom_size_func(1, 0.98, 0.9)
    }

for i in range(len(minor_sizes)):
    sizes['size_minor' + str(i)] = size_func(minor_sizes[i])
for i in range(8):
    sizes['size_subtle' + str(i)] = size_func(subtle_alleles)

# Define how likely the different species are to have each allele

# Define how likely each of the size alleles is to occur

equine_distribution = {
    'LCORL':[1],
    'HMGA2':[1],
    'donkey_extra': [1]
    }

for i in range(len(minor_sizes)):
    equine_distribution['size_minor' + str(i)] = [0.2, 0.4, 0.6, 0.8, 1]

for i in range(8):
    equine_distribution['size_subtle' + str(i)] = [
        0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]
    
for i in range(5):
    equine_distribution['size' + str(i)] = [1]
    
for i in range(7):
    equine_distribution['donkey_size' + str(i)] = [1]

# In donkeys
donkey_distribution = equine_distribution.copy()
donkey_distribution.update({
    'donkey_size0':[0.6, 0.8, 1],
    'donkey_size1':[0.6, 0.8, 1],
    'donkey_size2':[0.6, 0.8, 1],
    'donkey_size3':[0.9, 1],
    'donkey_size4':[0.8, 1],
    'donkey_size5':[0.8, 1],
    'donkey_size6':[0.9, 1],
    'donkey_extra':[0, 1]
    })

# In horses
horse_distribution = equine_distribution.copy()
# Based on the default horse breed
horse_distribution.update({
    'LCORL': [15/16, 1],
    'HMGA2': [15/16, 1],
    'size0': [0.6, 1],
    'size1': [0.6, 1],
    'size2': [0.4, 0.7, 0.85, 1],
    'size3': [0.6, 1],
    'size4': [0.4, 0.6, 0.8, 0.9, 1]    
    })


def max_size(distribution):
    '''Calculate the largest possible size the given genes can produce. '''
    # Assume longer legs increase height at scale=1 from 132 cm to 137 cm
    size = 137.0
    for gene in distribution:
        alleles = genetics.list_possible_alleles(distribution[gene])
        biggest = 0;
        for a in alleles:
            for b in alleles:
                x = sizes[gene]((a, b))
                if x > biggest:
                    biggest = x
        size *= biggest
    return size


def min_size(distribution):
    '''Calculate the smallest possible size the given genes can produce. '''
    # Assume shorter legs decrease height at scale=1 from 132 cm to 117 cm
    size = 117.0
    for gene in distribution:
        alleles = genetics.list_possible_alleles(distribution[gene])
        smallest = 10000000000;
        for a in alleles:
            for b in alleles:
                x = sizes[gene]((a, b))
                if x < smallest:
                    smallest = x
        size *= smallest
    return size
