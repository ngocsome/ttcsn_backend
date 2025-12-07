package org.example.model;

public class GaRunHistory {

    private long id;
    // Lưu thời gian dạng String cho dễ serialize JSON
    private String createdAt;
    private GeneticAlgorithmConfig config;
    private Graph graph;
    private MSTResult result;

    public GaRunHistory() {
    }

    public GaRunHistory(long id,
                        String createdAt,
                        GeneticAlgorithmConfig config,
                        Graph graph,
                        MSTResult result) {
        this.id = id;
        this.createdAt = createdAt;
        this.config = config;
        this.graph = graph;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public GeneticAlgorithmConfig getConfig() {
        return config;
    }

    public void setConfig(GeneticAlgorithmConfig config) {
        this.config = config;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public MSTResult getResult() {
        return result;
    }

    public void setResult(MSTResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "GaRunHistory{" +
                "id=" + id +
                ", createdAt='" + createdAt + '\'' +
                ", config=" + config +
                ", graph=" + graph +
                ", result=" + result +
                '}';
    }
}
