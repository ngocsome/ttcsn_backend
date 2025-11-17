package org.example.service;

import org.example.model.Edge;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.Individual;
import org.example.model.MSTResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmServiceImpl implements GeneticAlgorithmService {

    private final Random random = new Random();

    @Override
    public MSTResult solveMST(Graph graph, GeneticAlgorithmConfig config) {
        int popSize = config.getPopulationSize();
        int generations = config.getGenerations();

        int numEdges = graph.getEdges().size();

        List<Individual> population = initializePopulation(popSize, numEdges);

        double[] bestFitnessHistory = new double[generations];
        Individual bestOverall = null;

        for (int gen = 0; gen < generations; gen++) {
            evaluatePopulation(population, graph);
            population.sort((a, b) -> Double.compare(a.getFitness(), b.getFitness()));

            Individual bestThisGen = population.get(0);
            if (bestOverall == null || bestThisGen.getFitness() < bestOverall.getFitness()) {
                bestOverall = bestThisGen.copy();
            }

            bestFitnessHistory[gen] = bestOverall.getFitness();

            List<Individual> newPopulation = new ArrayList<>();
            // Elitism: giữ lại 1 cá thể tốt nhất
            newPopulation.add(bestOverall.copy());

            while (newPopulation.size() < popSize) {
                Individual parent1 = tournamentSelection(population);
                Individual parent2 = tournamentSelection(population);

                Individual child1 = parent1.copy();
                Individual child2 = parent2.copy();

                if (random.nextDouble() < config.getCrossoverRate()) {
                    onePointCrossover(child1, child2);
                }

                mutate(child1, config.getMutationRate());
                mutate(child2, config.getMutationRate());

                newPopulation.add(child1);
                if (newPopulation.size() < popSize) {
                    newPopulation.add(child2);
                }
            }

            population = newPopulation;
        }

        // Đánh giá bestOverall cuối cùng
        evaluateIndividual(bestOverall, graph);

        List<Edge> mstEdges = decodeIndividual(bestOverall, graph);
        double totalWeight = bestOverall.getFitness();
        boolean valid = isSpanningTree(bestOverall.getGenes(), graph);

        return new MSTResult(mstEdges, totalWeight, valid, bestFitnessHistory);
    }

    // ================== Core GA helper methods ==================

    private List<Individual> initializePopulation(int popSize, int numGenes) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            boolean[] genes = new boolean[numGenes];
            for (int j = 0; j < numGenes; j++) {
                genes[j] = random.nextBoolean();
            }
            population.add(new Individual(genes));
        }
        return population;
    }

    private void evaluatePopulation(List<Individual> population, Graph graph) {
        for (Individual ind : population) {
            evaluateIndividual(ind, graph);
        }
    }

    private void evaluateIndividual(Individual ind, Graph graph) {
        boolean[] genes = ind.getGenes();
        if (!isSpanningTree(genes, graph)) {
            // Nếu không phải cây bao trùm: phạt nặng
            ind.setFitness(1e9);
            return;
        }

        double totalWeight = 0.0;
        List<Edge> edges = graph.getEdges();
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                totalWeight += edges.get(i).getWeight();
            }
        }
        ind.setFitness(totalWeight);
    }

    private Individual tournamentSelection(List<Individual> population) {
        int tournamentSize = Math.min(3, population.size());
        List<Individual> picked = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            int idx = random.nextInt(population.size());
            picked.add(population.get(idx));
        }
        picked.sort((a, b) -> Double.compare(a.getFitness(), b.getFitness()));
        return picked.get(0);
    }

    private void onePointCrossover(Individual a, Individual b) {
        boolean[] ga = a.getGenes();
        boolean[] gb = b.getGenes();

        if (ga.length != gb.length || ga.length < 2) {
            return;
        }

        int point = random.nextInt(ga.length - 1) + 1; // 1..len-1
        for (int i = point; i < ga.length; i++) {
            boolean tmp = ga[i];
            ga[i] = gb[i];
            gb[i] = tmp;
        }
    }

    private void mutate(Individual ind, double mutationRate) {
        boolean[] genes = ind.getGenes();
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                genes[i] = !genes[i];
            }
        }
    }

    // ================== MST / feasibility helper ==================

    private boolean isSpanningTree(boolean[] genes, Graph graph) {
        int n = graph.getNumberOfVertices();
        List<Edge> edges = graph.getEdges();

        // Đếm số cạnh được chọn
        int countEdges = 0;
        for (boolean gene : genes) {
            if (gene) countEdges++;
        }
        // Cây bao trùm phải có n - 1 cạnh
        if (countEdges != n - 1) return false;

        // Dùng Union-Find kiểm tra có đúng 1 thành phần liên thông và không có chu trình
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                Edge e = edges.get(i);
                if (!uf.union(e.getU(), e.getV())) {
                    // Nếu union trả false => chu trình
                    return false;
                }
            }
        }

        // Kiểm tra tất cả đỉnh đều cùng 1 root
        int root = uf.find(0);
        for (int i = 1; i < n; i++) {
            if (uf.find(i) != root) {
                return false;
            }
        }

        return true;
    }

    private List<Edge> decodeIndividual(Individual ind, Graph graph) {
        List<Edge> result = new ArrayList<>();
        boolean[] genes = ind.getGenes();
        List<Edge> edges = graph.getEdges();
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                result.add(edges.get(i));
            }
        }
        return result;
    }

    // ================== Union-Find (Disjoint Set) ==================

    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // return false nếu x,y đã cùng tập (tạo chu trình)
        public boolean union(int x, int y) {
            int rx = find(x);
            int ry = find(y);
            if (rx == ry) return false;

            if (rank[rx] < rank[ry]) {
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rank[rx]++;
            }
            return true;
        }
    }
}
