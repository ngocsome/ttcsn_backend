package org.example.model;

import java.util.List;

public class MSTResult {

    private List<Edge> mstEdges;
    private double totalWeight;
    private boolean valid;
    private double[] bestFitnessPerGeneration;

    public MSTResult() {
    }

    public MSTResult(List<Edge> mstEdges, double totalWeight,
                     boolean valid, double[] bestFitnessPerGeneration) {
        this.mstEdges = mstEdges;
        this.totalWeight = totalWeight;
        this.valid = valid;
        this.bestFitnessPerGeneration = bestFitnessPerGeneration;
    }

    public List<Edge> getMstEdges() {
        return mstEdges;
    }

    public void setMstEdges(List<Edge> mstEdges) {
        this.mstEdges = mstEdges;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public double[] getBestFitnessPerGeneration() {
        return bestFitnessPerGeneration;
    }

    public void setBestFitnessPerGeneration(double[] bestFitnessPerGeneration) {
        this.bestFitnessPerGeneration = bestFitnessPerGeneration;
    }
}
