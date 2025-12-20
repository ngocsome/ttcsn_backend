package org.example.model.dto;

import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;

public class RunGaRequest {
    private GeneticAlgorithmConfig config;
    private Graph graph;

    public RunGaRequest() {}

    public GeneticAlgorithmConfig getConfig() { return config; }
    public void setConfig(GeneticAlgorithmConfig config) { this.config = config; }

    public Graph getGraph() { return graph; }
    public void setGraph(Graph graph) { this.graph = graph; }

    public GeneticAlgorithmConfig toConfig() { return config; }
    public Graph toGraph() { return graph; }

    @Override
    public String toString() {
        return "RunGaRequest{" + "config=" + config + ", graph=" + graph + '}';
    }
}
