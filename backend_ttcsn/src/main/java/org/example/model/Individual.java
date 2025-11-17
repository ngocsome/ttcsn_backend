package org.example.model;

import java.util.Arrays;

public class Individual {

    private boolean[] genes;
    private double fitness;

    public Individual() {
    }

    public Individual(boolean[] genes) {
        this.genes = genes;
        this.fitness = Double.POSITIVE_INFINITY;
    }

    public boolean[] getGenes() {
        return genes;
    }

    public void setGenes(boolean[] genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Individual copy() {
        boolean[] newGenes = Arrays.copyOf(this.genes, this.genes.length);
        Individual clone = new Individual(newGenes);
        clone.setFitness(this.fitness);
        return clone;
    }
}
