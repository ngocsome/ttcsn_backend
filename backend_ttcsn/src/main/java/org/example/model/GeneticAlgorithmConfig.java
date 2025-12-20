package org.example.model;

public class GeneticAlgorithmConfig {
    private int populationSize;
    private double crossoverRate;
    private double mutationRate;
    private int maxGenerations;

    public GeneticAlgorithmConfig() {}

    public GeneticAlgorithmConfig(int populationSize, double crossoverRate, double mutationRate, int maxGenerations) {
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.maxGenerations = maxGenerations;
    }

    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

    public double getCrossoverRate() { return crossoverRate; }
    public void setCrossoverRate(double crossoverRate) { this.crossoverRate = crossoverRate; }

    public double getMutationRate() { return mutationRate; }
    public void setMutationRate(double mutationRate) { this.mutationRate = mutationRate; }

    public int getMaxGenerations() { return maxGenerations; }
    public void setMaxGenerations(int maxGenerations) { this.maxGenerations = maxGenerations; }

    @Override
    public String toString() {
        return "GeneticAlgorithmConfig{" +
                "populationSize=" + populationSize +
                ", crossoverRate=" + crossoverRate +
                ", mutationRate=" + mutationRate +
                ", maxGenerations=" + maxGenerations +
                '}';
    }
}
