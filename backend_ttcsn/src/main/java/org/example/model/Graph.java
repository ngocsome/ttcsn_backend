package org.example.model;

import java.util.List;

public class Graph {

    private int vertexCount;
    private List<Edge> edges;

    public Graph() {
    }

    public Graph(int vertexCount, List<Edge> edges) {
        this.vertexCount = vertexCount;
        this.edges = edges;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "vertexCount=" + vertexCount +
                ", edges=" + edges +
                '}';
    }
}
