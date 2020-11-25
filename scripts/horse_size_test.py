import random


major_sizes = [[1, 1, 1, 1, 1.03], # LCORL
               [1, 1, 1, 1, 1, 1, 1, 1, 0.9], # HMGA2
               [1, 1.03], # Imprinted, maternal
               [1, 1.05], # Semi-dominant
               [1, 1.002, 1.03, 1.05],
               [1, 1/1.04], # Imprinted (paternal)
               [1, 1/1.005, 1/1.02, 1/1.05, 1/1.06] # Larger effects recessive
               ]

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

minor_sizes += [[1, 1.001, 1/1.001, 1.002, 1/1.002, 1.003, 1/1.003, 1.004, 1/1.004]] * 8

minor_large = [[n for n in x if n >= 1] for x in minor_sizes]
minor_small = [[n for n in x if n <= 1] for x in minor_sizes]

major_large = [[n for n in x if n >= 1] for x in major_sizes]
major_small = [[n for n in x if n <= 1] for x in major_sizes]

sizes = major_sizes + minor_sizes

def choose_random_size(sizes):
    size = 132.0
    for lst in sizes:
        num = random.choice(lst)
        size *= num
        num = random.choice(lst)
        size *= num

    return size


def max_size(sizes):
    # Assume longer legs increase height at scale=1 from 132 cm to 137 cm
    size = 137.0
    for lst in sizes:
        num = max(lst)
        size *= num * num

    return size


def min_size(sizes):
    # Assume shorter legs decrease height at scale=1 from 132 cm to 117 cm
    size = 117.0
    for lst in sizes:
        num = min(lst)
        size *= num * num

    return size
