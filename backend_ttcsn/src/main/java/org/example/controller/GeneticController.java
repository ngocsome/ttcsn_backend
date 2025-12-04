package org.example.controller;

import io.javalin.Javalin;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;
import org.example.model.dto.RunGaRequest;
import org.example.service.GeneticAlgorithmService;

public class GeneticController {

    public GeneticController(Javalin app, GeneticAlgorithmService service) {

        System.out.println("✅ Registering route POST /api/run-ga");

        app.post("/api/run-ga", ctx -> {
            try {
                System.out.println("👉 Received POST /api/run-ga");

                RunGaRequest request = ctx.bodyAsClass(RunGaRequest.class);
                System.out.println("📥 Request: " + request);

                Graph graph = request.toGraph();
                GeneticAlgorithmConfig config = request.toConfig();

                MSTResult result = service.solveMST(graph, config);

                ctx.json(result);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal error: " + e.getMessage());
            }
        });
    }
}
