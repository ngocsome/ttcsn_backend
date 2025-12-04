package org.example.model;

import java.util.List;

public class MSTResult {

    private double totalWeight;
    private int edgeCount;
    private int generations;
    private List<Edge> edges;

    public MSTResult() {
    }

    public MSTResult(double totalWeight, int edgeCount,
                     int generations, List<Edge> edges) {
        this.totalWeight = totalWeight;
        this.edgeCount = edgeCount;
        this.generations = generations;
        this.edges = edges;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
