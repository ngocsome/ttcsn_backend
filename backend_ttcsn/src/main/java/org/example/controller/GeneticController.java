package org.example.controller;

import io.javalin.Javalin;
import org.example.model.GaRunHistory;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;
import org.example.model.dto.RunGaRequest;
import org.example.model.dto.RunGaResponse;
import org.example.repository.GaRunHistoryStore;
import org.example.service.GeneticAlgorithmService;

public class GeneticController {

    // Lưu lịch sử bền vững vào file JSON (persist)
    private final GaRunHistoryStore historyStore;

    public GeneticController(Javalin app, GeneticAlgorithmService service) {

        System.out.println("✅ Registering routes for GA_MST");

        // File lưu lịch sử (bạn có thể đổi tên/path)
        this.historyStore = new GaRunHistoryStore("storage/ga_run_history.json");

        // 1) Chạy GA + lưu input/output
        app.post("/api/run-ga", ctx -> {
            try {
                RunGaRequest request = ctx.bodyAsClass(RunGaRequest.class);
                System.out.println("📥 Request: " + request);

                Graph graph = request.toGraph();
                GeneticAlgorithmConfig config = request.toConfig();

                // chạy GA-MST
                MSTResult result = service.solveMST(graph, config);

                // ✅ lưu input (graph+config) + output (result) xuống file
                GaRunHistory run = historyStore.add(config, graph, result);

                // trả về runId + result cho FE
                RunGaResponse response = new RunGaResponse(run.getId(), result);
                ctx.json(response);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal error: " + e.getMessage());
            }
        });

        // 2) Lấy danh sách lịch sử (đã lưu bền vững)
        app.get("/api/run-ga/history", ctx -> {
            ctx.json(historyStore.getAll());
        });

        // 3) Lấy chi tiết 1 lần chạy theo id
        app.get("/api/run-ga/history/{id}", ctx -> {
            try {
                long id = Long.parseLong(ctx.pathParam("id"));
                GaRunHistory found = historyStore.getById(id);

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
