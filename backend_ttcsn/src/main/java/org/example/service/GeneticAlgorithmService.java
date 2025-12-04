package org.example.service;

import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;

public interface GeneticAlgorithmService {
    MSTResult solveMST(Graph graph, GeneticAlgorithmConfig config);
}
