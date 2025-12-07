package org.example.controller;

import io.javalin.Javalin;
import org.example.model.GaRunHistory;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;
import org.example.model.dto.RunGaRequest;
import org.example.model.dto.RunGaResponse;
import org.example.service.GeneticAlgorithmService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GeneticController {

    // Lưu lịch sử trong RAM
    private final List<GaRunHistory> history = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public GeneticController(Javalin app, GeneticAlgorithmService service) {

        System.out.println("✅ Registering routes for GA_MST");

        // 1) Chạy GA + lưu lịch sử
        app.post("/api/run-ga", ctx -> {
            try {
                RunGaRequest request = ctx.bodyAsClass(RunGaRequest.class);
                System.out.println("📥 Request: " + request);

                Graph graph = request.toGraph();
                GeneticAlgorithmConfig config = request.toConfig();

                // gọi service giải MST
                MSTResult result = service.solveMST(graph, config);

                // lưu lịch sử
                long id = idGenerator.getAndIncrement();
                String now = java.time.LocalDateTime.now().toString();
                GaRunHistory run = new GaRunHistory(id, now, config, graph, result);
                history.add(run);

                // trả về cho FE runId + result
                RunGaResponse response = new RunGaResponse(id, result);
                ctx.json(response);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal error: " + e.getMessage());
            }
        });

        // 2) Lấy danh sách lịch sử
        app.get("/api/run-ga/history", ctx -> {
            ctx.json(history);
        });

        // 3) Lấy chi tiết 1 lần chạy theo id
        app.get("/api/run-ga/history/{id}", ctx -> {
            try {
                long id = Long.parseLong(ctx.pathParam("id"));
                GaRunHistory found = history.stream()
                        .filter(h -> h.getId() == id)
                        .findFirst()
                        .orElse(null);

                if (found == null) {
                    ctx.status(404).result("Run not found");
                } else {
                    ctx.json(found);
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid id");
            }
        });
    }
}
