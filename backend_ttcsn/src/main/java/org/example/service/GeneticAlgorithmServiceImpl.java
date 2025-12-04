package org.example.service;

import org.example.model.*;

import java.util.*;

public class GeneticAlgorithmServiceImpl implements GeneticAlgorithmService {

    private static final double BIG_PENALTY = 1e12;

    @Override
    public MSTResult solveMST(Graph graph, GeneticAlgorithmConfig config) {
        Objects.requireNonNull(graph, "graph must not be null");
        Objects.requireNonNull(config, "config must not be null");

        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges != null ? edges.size() : 0;

        if (n <= 1 || m == 0) {
            return new MSTResult(0, 0, 0, Collections.emptyList());
        }

        int popSize = Math.max(10, config.getPopulationSize());
        double pc = config.getCrossoverRate();
        double pm = config.getMutationRate();
        int maxGen = Math.max(1, config.getMaxGenerations());

        Random random = new Random();

        // 1. Khởi tạo quần thể
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            boolean[] chromosome = createRandomTreeChromosome(graph, random);
            Individual ind = new Individual(chromosome);
            evaluateIndividual(ind, graph);
            population.add(ind);
        }

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

        MSTResult result = new MSTResult(totalWeight, edgeCount, bestGen, bestEdges);
        System.out.println("✅ GA finished: weight=" + totalWeight +
                ", edges=" + edgeCount + ", bestGen=" + bestGen);
        return result;
    }

    // ========== GA Helpers ==========

    private boolean[] createRandomTreeChromosome(Graph graph, Random random) {
        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges.size();
        boolean[] chromosome = new boolean[m];

        // DSU
        int[] parent = initParent(n);
        int edgesUsed = 0;

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < m; i++) indices.add(i);
        Collections.shuffle(indices, random);

        for (int idx : indices) {
            if (edgesUsed == n - 1) break;
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
                sum += edges.get(i).getWeight();
            }
        }
        return sum;
    }

    private boolean isValidSpanningTree(boolean[] chromosome, Graph graph) {
        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int[] parent = initParent(n);
        int used = 0;

        for (int i = 0; i < chromosome.length; i++) {
            if (!chromosome[i]) continue;
            Edge e = edges.get(i);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru == rv) {
                return false; // có chu trình
            }
            parent[ru] = rv;
            used++;
        }

        if (used != n - 1) return false;

        int root = -1;
        for (int i = 0; i < n; i++) {
            int r = find(parent, i);
            if (root == -1) root = r;
            else if (r != root) return false; // không liên thông
        }
        return true;
    }

    private Individual tournamentSelection(List<Individual> pop, Random random, int k) {
        Individual best = null;
        for (int i = 0; i < k; i++) {
            Individual cand = pop.get(random.nextInt(pop.size()));
            if (best == null || cand.getCost() < best.getCost()) {
                best = cand;
            }
        }
        return best;
    }

    private Individual[] crossover(Individual p1, Individual p2,
                                   double pc, Random random) {
        boolean[] a = p1.getChromosome();
        boolean[] b = p2.getChromosome();
        int len = a.length;

        if (random.nextDouble() >= pc) {
            return new Individual[]{p1.copy(), p2.copy()};
        }

        int point = random.nextInt(len);
        boolean[] c1 = new boolean[len];
        boolean[] c2 = new boolean[len];

        for (int i = 0; i < len; i++) {
            if (i < point) {
                c1[i] = a[i];
                c2[i] = b[i];
            } else {
                c1[i] = b[i];
                c2[i] = a[i];
            }
        }

        return new Individual[]{new Individual(c1), new Individual(c2)};
    }

    private void mutate(Individual ind, double pm, Random random) {
        boolean[] c = ind.getChromosome();
        for (int i = 0; i < c.length; i++) {
            if (random.nextDouble() < pm) {
                c[i] = !c[i];
            }
        }
    }

    // "Sửa" chromosome -> luôn thành cây khung hợp lệ
    private void repairChromosome(boolean[] chromosome, Graph graph, Random random) {
        int n = graph.getVertexCount();
        List<Edge> edges = graph.getEdges();
        int m = edges.size();

        int[] parent = initParent(n);
        int used = 0;

        // 1. Giữ lại các cạnh đang bật nếu không tạo chu trình
        for (int i = 0; i < m; i++) {
            if (!chromosome[i]) continue;
            Edge e = edges.get(i);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru != rv) {
                parent[ru] = rv;
                used++;
            } else {
                chromosome[i] = false;
            }
        }

        // 2. Thêm cạnh mới cho đủ n-1 cạnh và liên thông
        List<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < m; i++) idxs.add(i);
        Collections.shuffle(idxs, random);

        for (int idx : idxs) {
            if (used == n - 1) break;
            if (chromosome[idx]) continue;
            Edge e = edges.get(idx);
            int ru = find(parent, e.getU());
            int rv = find(parent, e.getV());
            if (ru != rv) {
                parent[ru] = rv;
                chromosome[idx] = true;
                used++;
            }
        }
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

    // DSU helpers
    private int[] initParent(int n) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
        return p;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }
}
