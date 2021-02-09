import random

import equine_sizes
from genetics import *

def choose_random_size(distribution):
    size = 132
    for gene in distribution:
        frequency = distribution[gene]
        alleles = (choose_random_allele(frequency),
                   choose_random_allele(frequency))
        size *= equine_sizes.sizes[gene](alleles)
    return size

def percentile_sizes(distribution, n, percentiles=[0.1, 0.5, 0.9]):
    horse_sizes = []
    for i in range(n):
        horse_sizes += [choose_random_size(distribution)]
    horse_sizes.sort()
    nums = []
    for p in percentiles:
        nums += [horse_sizes[int(n * p)]]
    return nums

def get_size(horse):
    '''Calculates the horse's height in cm based on its genes'''
    size = 132
    for gene in horse:
        size *= equine_sizes.sizes[gene](horse[gene])
    return size


def breed_towards(distribution, population_size, num_children, done, select,
                  max_generations=200):
    '''A generalized function for selective breeding.
        done - a function that takes a population (list of animals) and
        returns true if it satisfies the goal of the breeding
        select - a function that takes a group of animals and returns one to
        keep for the next generation.
    This implementation always ensures each horse in the previous generation
    has at least one child in the next generation.'''
    # Set up a breeding herd
    generations = 0;
    pops = []
    for i in range(population_size):
        pops += [generate_animal(distribution)]
    while generations < max_generations and not done(pops) :
        generations += 1;
        next_pops = []
        for horse in pops:
            foals = []
            for i in range(num_children):
                foals += [get_child(horse, random.choice(pops))]
            next_pops += [select(foals)]
        pops = next_pops
    print('Generations: ' + str(generations))
    return get_distribution(pops)

def within_size_range(min_height, max_height, p=0.05):
    '''Returns a function that takes a population and returns true if
    at least 1-p of the population are a bove min_height and 1-p are below
    max_height'''
    def done(herd):
        herd.sort(key=get_size)
        if get_size(herd[int(len(herd) * p)]) < min_height:
            return False
        if get_size(herd[int(len(herd) * (1-p))]) > max_height:
            return False
        return True
    return done

def select_size_range(min_height, max_height):
    '''First, attempts to randomly choose a horse within the given size range.
    If there is none, it picks the horse closest to the geometric center of
    the range.'''
    def select(herd):
        ok = []
        for h in herd:
            size = get_size(h)
            if (size >= min_height and size <= max_height):
                ok += [h]
        if ok:
            return random.choice(ok)
        # Geometric average
        avg = min_height ** 0.5 * max_height ** 0.5
        def distance_from_average(h):
            if avg <= 0:
                return size - avg
            if size <= 0:
                return float('inf')
            return max(size / avg, avg / size)
        return min(herd, key=distance_from_average)
        

    return select
