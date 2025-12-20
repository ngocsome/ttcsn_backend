package org.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.GaRunHistory;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GaRunHistoryRepository {

    private final String jdbcUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public GaRunHistoryRepository(String dbFilePath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbFilePath;
        init();
    }

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void init() {
        String sql =
                "CREATE TABLE IF NOT EXISTS ga_run_history (" +
                        "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  created_at TEXT NOT NULL," +
                        "  config_json TEXT NOT NULL," +
                        "  graph_json  TEXT NOT NULL," +
                        "  result_json TEXT NOT NULL" +
                        ");";
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Init DB failed: " + e.getMessage(), e);
        }
    }

    public long insert(GeneticAlgorithmConfig config, Graph graph, MSTResult result) {
        try {
            String createdAt = LocalDateTime.now().toString();
            String configJson = mapper.writeValueAsString(config);
            String graphJson  = mapper.writeValueAsString(graph);
            String resultJson = mapper.writeValueAsString(result);

            String sql = "INSERT INTO ga_run_history(created_at, config_json, graph_json, result_json) VALUES(?,?,?,?)";
            try (Connection c = conn();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, createdAt);
                ps.setString(2, configJson);
                ps.setString(3, graphJson);
                ps.setString(4, resultJson);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
            return -1;

        } catch (Exception e) {
            throw new RuntimeException("Insert failed: " + e.getMessage(), e);
        }
    }

    public List<GaRunHistory> findAll() {
        String sql = "SELECT id, created_at, config_json, graph_json, result_json FROM ga_run_history ORDER BY id DESC";
        List<GaRunHistory> out = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String createdAt = rs.getString("created_at");

                GeneticAlgorithmConfig config = mapper.readValue(rs.getString("config_json"), GeneticAlgorithmConfig.class);
                Graph graph = mapper.readValue(rs.getString("graph_json"), Graph.class);
                MSTResult result = mapper.readValue(rs.getString("result_json"), MSTResult.class);

                out.add(new GaRunHistory(id, createdAt, config, graph, result));
            }
            return out;

        } catch (Exception e) {
            throw new RuntimeException("FindAll failed: " + e.getMessage(), e);
        }
    }

    public GaRunHistory findById(long runId) {
        String sql = "SELECT id, created_at, config_json, graph_json, result_json FROM ga_run_history WHERE id = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                long id = rs.getLong("id");
                String createdAt = rs.getString("created_at");

                GeneticAlgorithmConfig config = mapper.readValue(rs.getString("config_json"), GeneticAlgorithmConfig.class);
                Graph graph = mapper.readValue(rs.getString("graph_json"), Graph.class);
                MSTResult result = mapper.readValue(rs.getString("result_json"), MSTResult.class);

                return new GaRunHistory(id, createdAt, config, graph, result);
            }

        } catch (Exception e) {
            throw new RuntimeException("FindById failed: " + e.getMessage(), e);
        }
    }
}
