package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.example.model.GaRunHistory;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;
import org.example.model.dto.RunGaRequest;
import org.example.model.dto.RunGaResponse;
import org.example.repository.GaRunHistoryRepository;
import org.example.service.GeneticAlgorithmService;

import java.util.List;

public class GeneticController {

    private final GaRunHistoryRepository repo = new GaRunHistoryRepository("storage/ga_history.db");
    private final ObjectMapper mapper = new ObjectMapper();

    public GeneticController(Javalin app, GeneticAlgorithmService service) {

        // 1) Run GA + lưu input/output vào bảng SQLite
        app.post("/api/run-ga", ctx -> {
            RunGaRequest request = ctx.bodyAsClass(RunGaRequest.class);

            Graph graph = request.toGraph();
            GeneticAlgorithmConfig config = request.toConfig();

            MSTResult result = service.solveMST(graph, config);

            long runId = repo.insert(config, graph, result);

            ctx.json(new RunGaResponse(runId, result));
        });

        // 2) Lấy danh sách lịch sử (từ DB)
        app.get("/api/run-ga/history", ctx -> {
            ctx.json(repo.findAll());
        });

        // 3) Lấy chi tiết 1 run
        app.get("/api/run-ga/history/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            GaRunHistory found = repo.findById(id);
            if (found == null) ctx.status(404).result("Run not found");
            else ctx.json(found);
        });

        // 4) Export CSV (mở Excel -> bảng để nộp báo cáo)
        app.get("/api/run-ga/history.csv", ctx -> {
            List<GaRunHistory> history = repo.findAll();

            StringBuilder sb = new StringBuilder();
            sb.append("runId,createdAt,nVertices,nEdges,populationSize,crossoverRate,mutationRate,maxGenerations,totalWeight,timeMs,bestGen\n");

            for (GaRunHistory h : history) {
                JsonNode cfgNode = mapper.valueToTree(h.getConfig());
                JsonNode graphNode = mapper.valueToTree(h.getGraph());
                JsonNode rsNode  = mapper.valueToTree(h.getResult());

                String nV = safe(graphNode, "vertexCount", "vertex_count", "nVertices");
                String nE = "";
                JsonNode edgesNode = graphNode.get("edges");
                if (edgesNode != null && edgesNode.isArray()) nE = String.valueOf(edgesNode.size());

                String pop = safe(cfgNode, "populationSize");
                String cr  = safe(cfgNode, "crossoverRate");
                String mr  = safe(cfgNode, "mutationRate");
                String mg  = safe(cfgNode, "maxGenerations");

                String totalW = safe(rsNode, "totalWeight", "weight");
                String timeMs = safe(rsNode, "timeMs", "runtimeMs");
                String bestG  = safe(rsNode, "bestGen", "bestGeneration");

                sb.append(h.getId()).append(",")
                        .append(csvEscape(h.getCreatedAt())).append(",")
                        .append(csvEscape(nV)).append(",")
                        .append(csvEscape(nE)).append(",")
                        .append(csvEscape(pop)).append(",")
                        .append(csvEscape(cr)).append(",")
                        .append(csvEscape(mr)).append(",")
                        .append(csvEscape(mg)).append(",")
                        .append(csvEscape(totalW)).append(",")
                        .append(csvEscape(timeMs)).append(",")
                        .append(csvEscape(bestG))
                        .append("\n");
            }

            // BOM để Excel đọc tiếng Việt chuẩn
            String csv = "\uFEFF" + sb;
            ctx.header("Content-Disposition", "attachment; filename=ga_run_history.csv");
            ctx.contentType("text/csv; charset=utf-8");
            ctx.result(csv);
        });

        // 5) Export HTML Table (copy/paste sang Word giữ nguyên bảng)
        app.get("/api/run-ga/history-table", ctx -> {
            List<GaRunHistory> history = repo.findAll();

            StringBuilder html = new StringBuilder();
            html.append("<!doctype html><html><head><meta charset='utf-8'>")
                    .append("<style>")
                    .append("body{font-family:Arial; padding:16px} table{border-collapse:collapse; width:100%}")
                    .append("th,td{border:1px solid #999; padding:8px; font-size:14px}")
                    .append("th{background:#f2f2f2}")
                    .append("</style></head><body>")
                    .append("<h2>GA_MST Run History</h2>")
                    .append("<table><thead><tr>")
                    .append("<th>RunId</th><th>CreatedAt</th><th>N</th><th>M</th>")
                    .append("<th>Pop</th><th>Pc</th><th>Pm</th><th>MaxGen</th>")
                    .append("<th>TotalWeight</th><th>Time(ms)</th><th>BestGen</th>")
                    .append("</tr></thead><tbody>");

            for (GaRunHistory h : history) {
                JsonNode cfgNode = mapper.valueToTree(h.getConfig());
                JsonNode graphNode = mapper.valueToTree(h.getGraph());
                JsonNode rsNode  = mapper.valueToTree(h.getResult());

                String nV = safe(graphNode, "vertexCount", "vertex_count", "nVertices");
                String nE = "";
                JsonNode edgesNode = graphNode.get("edges");
                if (edgesNode != null && edgesNode.isArray()) nE = String.valueOf(edgesNode.size());

                String pop = safe(cfgNode, "populationSize");
                String cr  = safe(cfgNode, "crossoverRate");
                String mr  = safe(cfgNode, "mutationRate");
                String mg  = safe(cfgNode, "maxGenerations");

                String totalW = safe(rsNode, "totalWeight", "weight");
                String timeMs = safe(rsNode, "timeMs", "runtimeMs");
                String bestG  = safe(rsNode, "bestGen", "bestGeneration");

                html.append("<tr>")
                        .append("<td>").append(h.getId()).append("</td>")
                        .append("<td>").append(h.getCreatedAt()).append("</td>")
                        .append("<td>").append(nV).append("</td>")
                        .append("<td>").append(nE).append("</td>")
                        .append("<td>").append(pop).append("</td>")
                        .append("<td>").append(cr).append("</td>")
                        .append("<td>").append(mr).append("</td>")
                        .append("<td>").append(mg).append("</td>")
                        .append("<td>").append(totalW).append("</td>")
                        .append("<td>").append(timeMs).append("</td>")
                        .append("<td>").append(bestG).append("</td>")
                        .append("</tr>");
            }

            html.append("</tbody></table></body></html>");
            ctx.contentType("text/html; charset=utf-8");
            ctx.result(html.toString());
        });
    }

    private static String csvEscape(String s) {
        if (s == null) return "\"\"";
        String out = s.replace("\"", "\"\"");
        return "\"" + out + "\"";
    }

    private static String safe(JsonNode node, String... keys) {
        if (node == null) return "";
        for (String k : keys) {
            JsonNode v = node.get(k);
            if (v != null && !v.isNull()) return v.asText();
        }
        return "";
    }
}
