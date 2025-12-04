package org.example.model;

import java.util.Arrays;

public class Individual {

    private boolean[] chromosome;
    private double fitness;
    private double cost;

    public Individual(boolean[] chromosome) {
        this.chromosome = chromosome;
    }

    public boolean[] getChromosome() {
        return chromosome;
    }

    public void setChromosome(boolean[] chromosome) {
        this.chromosome = chromosome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Individual copy() {
        Individual clone = new Individual(Arrays.copyOf(chromosome, chromosome.length));
        clone.fitness = this.fitness;
        clone.cost = this.cost;
        return clone;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "cost=" + cost +
                ", fitness=" + fitness +
                '}';
    }
}
