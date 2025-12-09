package org.example.service;

import org.example.model.*;

import java.util.*;

public class GeneticAlgorithmServiceImpl implements GeneticAlgorithmService {

    private static final double BIG_PENALTY = 1e12;

    @Override
    public MSTResult solveMST(Graph graph, GeneticAlgorithmConfig config) {
        Objects.requireNonNull(graph, "graph must not be null");
        Objects.requireNonNull(config, "config must not be null");

        long startTime = System.nanoTime();

        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges != null ? edges.size() : 0;

        if (n <= 1 || m == 0) {
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult(0, 0, 0, Collections.emptyList(), timeMs,
                    Collections.emptyList(), Collections.emptyList());
        }

        int popSize = Math.max(10, config.getPopulationSize());
        double pc = config.getCrossoverRate();
        double pm = config.getMutationRate();
        int maxGen = Math.max(1, config.getMaxGenerations());

        Random random = new Random();

        // Theo dõi quá trình hội tụ
        List<Double> bestFitnessHistory = new ArrayList<>();
        List<Double> avgFitnessHistory = new ArrayList<>();

        // 1. Khởi tạo quần thể
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            boolean[] chromosome = createRandomTreeChromosome(graph, random);
            Individual ind = new Individual(chromosome);
            evaluateIndividual(ind, graph);
            population.add(ind);
        }

        // Thống kê hội tụ cho thế hệ 0 (quần thể ban đầu)
        double bestCostGen0 = Double.POSITIVE_INFINITY;
        double sumCostGen0 = 0.0;
        for (Individual ind : population) {
            double c = ind.getCost();
            sumCostGen0 += c;
            if (c < bestCostGen0) {
                bestCostGen0 = c;
            }
        }
        bestFitnessHistory.add(bestCostGen0);
        avgFitnessHistory.add(sumCostGen0 / population.size());

        Individual best = findBest(population);
        int bestGen = 0;

        // 2. Vòng lặp tiến hoá
        for (int gen = 1; gen <= maxGen; gen++) {
            List<Individual> newPop = new ArrayList<>();

            // elitism: giữ lại 1 cá thể tốt nhất
            newPop.add(best.copy());

            while (newPop.size() < popSize) {
                Individual parent1 = tournamentSelection(population, random, 3);
                Individual parent2 = tournamentSelection(population, random, 3);

                Individual[] children = crossover(parent1, parent2, pc, random);

                for (Individual child : children) {
                    mutate(child, pm, random);
                    repairChromosome(child.getChromosome(), graph, random);
                    evaluateIndividual(child, graph);
                    newPop.add(child);
                    if (newPop.size() >= popSize) break;
                }
            }

            population = newPop;

            // Thống kê hội tụ cho thế hệ hiện tại
            double genBestCost = Double.POSITIVE_INFINITY;
            double genSumCost = 0.0;
            for (Individual ind : population) {
                double c = ind.getCost();
                genSumCost += c;
                if (c < genBestCost) {
                    genBestCost = c;
                }
            }
            bestFitnessHistory.add(genBestCost);
            avgFitnessHistory.add(genSumCost / population.size());

            Individual currentBest = findBest(population);
            if (currentBest.getCost() < best.getCost()) {
                best = currentBest.copy();
                bestGen = gen;
            }
        }

        // Lấy danh sách cạnh từ best
        List<Edge> bestEdges = decodeEdges(best.getChromosome(), graph);
        double totalWeight = best.getCost();
        int edgeCount = bestEdges.size();

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        MSTResult result = new MSTResult(
                totalWeight,
                edgeCount,
                bestGen,
                bestEdges,
                timeMs,
                bestFitnessHistory,
                avgFitnessHistory
        );
        System.out.println(" GA finished: weight=" + totalWeight +
                ", edges=" + edgeCount + ", bestGen=" + bestGen +
                ", timeMs=" + timeMs + " ms");
        return result;
    }

    // ========== GA Helpers ==========

    private boolean[] createRandomTreeChromosome(Graph graph, Random random) {
        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges.size();

        boolean[] chromosome = new boolean[m];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int edgesUsed = 0;
        while (edgesUsed < n - 1) {
            int idx = random.nextInt(m);
            if (chromosome[idx]) continue;
            Edge e = edges.get(idx);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru != rv) {
                parent[ru] = rv;
                chromosome[idx] = true;
                edgesUsed++;
            }
        }
        return chromosome;
    }

    private void evaluateIndividual(Individual ind, Graph graph) {
        boolean[] chromosome = ind.getChromosome();
        if (!isValidSpanningTree(chromosome, graph)) {
            ind.setCost(BIG_PENALTY);
            ind.setFitness(1.0 / (1.0 + BIG_PENALTY));
            return;
        }
        double cost = computeCost(chromosome, graph);
        ind.setCost(cost);
        ind.setFitness(1.0 / (1.0 + cost));
    }

    private double computeCost(boolean[] chromosome, Graph graph) {
        double sum = 0.0;
        List<Edge> edges = graph.getEdges();
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i]) {
                Edge e = edges.get(i);
                sum += e.getWeight();
            }
        }
        return sum;
    }

    private Individual findBest(List<Individual> population) {
        Individual best = population.get(0);
        for (Individual ind : population) {
            if (ind.getCost() < best.getCost()) {
                best = ind;
            }
        }
        return best;
    }

    private Individual tournamentSelection(List<Individual> population, Random random, int k) {
        Individual best = null;
        for (int i = 0; i < k; i++) {
            Individual candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }

    private Individual[] crossover(Individual p1, Individual p2, double pc, Random random) {
        boolean[] c1 = Arrays.copyOf(p1.getChromosome(), p1.getChromosome().length);
        boolean[] c2 = Arrays.copyOf(p2.getChromosome(), p2.getChromosome().length);

        if (random.nextDouble() < pc) {
            int point = random.nextInt(c1.length);
            for (int i = point; i < c1.length; i++) {
                boolean temp = c1[i];
                c1[i] = c2[i];
                c2[i] = temp;
            }
        }

        return new Individual[]{new Individual(c1), new Individual(c2)};
    }

    private void mutate(Individual ind, double pm, Random random) {
        boolean[] chromosome = ind.getChromosome();
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < pm) {
                chromosome[i] = !chromosome[i];
            }
        }
    }

    private void repairChromosome(boolean[] chromosome, Graph graph, Random random) {
        if (isValidSpanningTree(chromosome, graph)) return;

        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges.size();

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        Arrays.fill(parent, -1);
        Arrays.fill(chromosome, false);

        int edgesUsed = 0;
        while (edgesUsed < n - 1) {
            int idx = random.nextInt(m);
            Edge e = edges.get(idx);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru != rv) {
                parent[ru] = rv;
                chromosome[idx] = true;
                edgesUsed++;
            }
        }
    }

    private boolean isValidSpanningTree(boolean[] chromosome, Graph graph) {
        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int edgesUsed = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (!chromosome[i]) continue;
            Edge e = edges.get(i);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru == rv) {
                return false;
            }
            parent[ru] = rv;
            edgesUsed++;
        }
        return edgesUsed == n - 1;
    }

    private List<Edge> decodeEdges(boolean[] chromosome, Graph graph) {
        List<Edge> result = new ArrayList<>();
        List<Edge> edges = graph.getEdges();
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i]) {
                result.add(edges.get(i));
            }
        }
        return result;
    }

    private int find(int[] parent, int x) {
        if (parent[x] == x || parent[x] == -1) return x;
        parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    // Bạn có thể có lớp Individual riêng trong package model;
    // ở đây giả sử đã có đầy đủ getCost(), getFitness(), getChromosome(), copy(), ...
}
