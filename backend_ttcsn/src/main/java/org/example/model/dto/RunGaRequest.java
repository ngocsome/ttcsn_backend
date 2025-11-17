package org.example.model.dto;


import org.example.model.Edge;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;

import java.util.List;

public class RunGaRequest {

    private int numberOfVertices;
    private List<Edge> edges;

    private int populationSize;
    private int generations;
    private double mutationRate;
    private double crossoverRate;

    public RunGaRequest() {
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public void setNumberOfVertices(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    // Chuyển sang Graph để service xử lý
    public Graph toGraph() {
        return new Graph(numberOfVertices, edges);
    }

    // Chuyển sang Config để GA chạy
    public GeneticAlgorithmConfig toConfig() {
        return new GeneticAlgorithmConfig(
                populationSize,
                generations,
                mutationRate,
                crossoverRate
        );
    }
}
