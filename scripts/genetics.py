'''Functions for working with data structures representing genes
An animal is represented as a dictionary where each gene is a key mapping to
an iterable of its alleles
Example animal: {'gene1':(0, 1), 'gene2':(0,0)}
An allele frequency is a list of probabilities
Example frequency: [0.2, 0.5, 1] means a 0.2 chance of 0, a 0.3 chance of 1,
and a 0.5 chance of 2
A distribution or breed is a dictionary mapping genes to frequencies'''
import random

def choose_random_allele(frequency):
    '''Frequency is a list of numbers between 0 and 1'''
    r = random.uniform(0, 1)
    for i in range(len(frequency)):
        if r < frequency[i]:
            return i
    return 0

def list_possible_alleles(frequency):
    '''Returns a list of alleles with non-zero chance of being chosen
    from the given allele frequency'''
    lst = []
    max_prev = 0
    for i in range(len(frequency)):
        if frequency[i] > max_prev:
            max_prev = frequency[i]
            lst += [i]
    return lst

def generate_animal(distribution):
    '''Randomly creates an animal based on the given distribution of genes'''
    animal = {}
    for gene in distribution:
        frequency = distribution[gene]
        animal[gene] = (choose_random_allele(frequency),
                   choose_random_allele(frequency))
    return animal

def get_child(mother, father):
    '''Breeds the mother and father and returns their child'''
    child = {}
    for gene in mother:
        m = random.choice(mother[gene])
        f = random.choice(father[gene])
        child[gene] = (m, f)
    return child

def get_distribution(population):
    '''Calculates the allele frequencies in the given group of animals'''
    # For each gene keep a list counting how many times each allele appears
    total = {}
    for h in population:
        for gene in h:
            if not (gene in total):
                total[gene] = []
            for allele in h[gene]:
                while len(total[gene]) <= allele:
                    total[gene] += [0]
                total[gene][allele] += 1
    distribution = {}
    for gene in total:
        # First count all the alleles
        final = [total[gene][0]]
        for i in range(1, len(total[gene])):
            final += [total[gene][i]]
            final[i] += final[i - 1]
        # Then divide by the total number. Doing this afterwards means less
        # floating point arithmetic inaccuracies.
        s = sum(total[gene])
        for i in range(len(final)):
            final[i] /= s
        distribution[gene] = final
    return distribution
