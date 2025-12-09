package org.example.model;

import java.util.List;

/**
 * Kết quả chạy thuật toán GA cho bài toán cây khung nhỏ nhất.
 * Bổ sung thêm:
 *  - executionTimeMs       : thời gian thực hiện (ms)
 *  - bestFitnessHistory    : lịch sử giá trị tốt nhất theo từng thế hệ
 *  - avgFitnessHistory     : lịch sử giá trị trung bình theo từng thế hệ
 */
public class MSTResult {

    private double totalWeight;                 // tổng trọng số cây khung tốt nhất
    private int edgeCount;                      // số cạnh của cây khung
    private int generations;                    // thế hệ đạt được nghiệm tốt nhất
    private List<Edge> edges;                   // danh sách cạnh của cây khung

    // Thông tin phục vụ vẽ biểu đồ
    private double executionTimeMs;             // thời gian chạy thuật toán (ms)
    private List<Double> bestFitnessHistory;    // best "fitness"/cost mỗi thế hệ
    private List<Double> avgFitnessHistory;     // avg "fitness"/cost mỗi thế hệ

    public MSTResult() {
    }

    /**
     * Constructor cũ - dùng khi không cần thông tin biểu đồ.
     */
    public MSTResult(double totalWeight, int edgeCount,
                     int generations, List<Edge> edges) {
        this.totalWeight = totalWeight;
        this.edgeCount = edgeCount;
        this.generations = generations;
        this.edges = edges;
    }

    /**
     * Constructor đầy đủ – dùng cho GA khi muốn trả thêm thời gian chạy và lịch sử hội tụ.
     */
    public MSTResult(double totalWeight,
                     int edgeCount,
                     int generations,
                     List<Edge> edges,
                     double executionTimeMs,
                     List<Double> bestFitnessHistory,
                     List<Double> avgFitnessHistory) {
        this.totalWeight = totalWeight;
        this.edgeCount = edgeCount;
        this.generations = generations;
        this.edges = edges;
        this.executionTimeMs = executionTimeMs;
        this.bestFitnessHistory = bestFitnessHistory;
        this.avgFitnessHistory = avgFitnessHistory;
    }

    // ===== Getters & Setters cơ bản =====

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

    public double getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(double executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public List<Double> getBestFitnessHistory() {
        return bestFitnessHistory;
    }

    public void setBestFitnessHistory(List<Double> bestFitnessHistory) {
        this.bestFitnessHistory = bestFitnessHistory;
    }

    public List<Double> getAvgFitnessHistory() {
        return avgFitnessHistory;
    }

    public void setAvgFitnessHistory(List<Double> avgFitnessHistory) {
        this.avgFitnessHistory = avgFitnessHistory;
    }
}
