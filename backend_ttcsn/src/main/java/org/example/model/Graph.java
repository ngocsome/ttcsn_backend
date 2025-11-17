package org.example.model;

import java.util.List;

public class Graph {

    private int numberOfVertices;
    private List<Edge> edges;

    public Graph() {
    }

    public Graph(int numberOfVertices, List<Edge> edges) {
        this.numberOfVertices = numberOfVertices;
        this.edges = edges;
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
}
