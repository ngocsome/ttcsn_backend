package org.example.controller;

import io.javalin.Javalin;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;
import org.example.model.dto.RunGaRequest;
import org.example.service.GeneticAlgorithmService;

public class GeneticController {

    public GeneticController(Javalin app, GeneticAlgorithmService service) {

        System.out.println("✅ Registering route POST /api/run-ga"); // log cho chắc

        app.post("/api/run-ga", ctx -> {
            // Nhận JSON từ FE
            RunGaRequest request = ctx.bodyAsClass(RunGaRequest.class);

            // Chuyển sang Graph + Config
            Graph graph = request.toGraph();
            GeneticAlgorithmConfig config = request.toConfig();

            // Gọi service GA
            MSTResult result = service.solveMST(graph, config);

            // Trả JSON về cho client
            ctx.json(result);
        });
    }
}
