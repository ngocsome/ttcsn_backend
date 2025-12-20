package org.example.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.GaRunHistory;
import org.example.model.GeneticAlgorithmConfig;
import org.example.model.Graph;
import org.example.model.MSTResult;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class GaRunHistoryStore {

    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    // cache trong RAM để API trả nhanh
    private final List<GaRunHistory> cache = new ArrayList<>();
    private long nextId = 1;

    public GaRunHistoryStore(String filePath) {
        this(Paths.get(filePath));
    }

    public GaRunHistoryStore(Path filePath) {
        this.filePath = filePath;
        loadFromDisk();
    }

    private synchronized void loadFromDisk() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath) || Files.size(filePath) == 0) {
                // chưa có file -> cache rỗng
                cache.clear();
                nextId = 1;
                return;
            }

            List<GaRunHistory> list = mapper.readValue(
                    Files.readAllBytes(filePath),
                    new TypeReference<List<GaRunHistory>>() {}
            );

            cache.clear();
            cache.addAll(list);

            long maxId = cache.stream().mapToLong(GaRunHistory::getId).max().orElse(0);
            nextId = maxId + 1;

        } catch (Exception e) {
            // nếu file hỏng JSON -> không cho crash server, chỉ reset
            cache.clear();
            nextId = 1;
            System.err.println("⚠️ Cannot load history file: " + e.getMessage());
        }
    }

    private synchronized void saveToDisk() {
        try {
            byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(cache);
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save history file: " + e.getMessage(), e);
        }
    }

    public synchronized GaRunHistory add(GeneticAlgorithmConfig config, Graph graph, MSTResult result) {
        long id = nextId++;
        String now = LocalDateTime.now().toString();

        GaRunHistory run = new GaRunHistory(id, now, config, graph, result);
        cache.add(run);
        saveToDisk();
        return run;
    }

    public synchronized List<GaRunHistory> getAll() {
        // trả bản copy để tránh bị sửa ngoài ý muốn
        return new ArrayList<>(cache);
    }

    public synchronized GaRunHistory getById(long id) {
        for (GaRunHistory h : cache) {
            if (h.getId() == id) return h;
        }
        return null;
    }
}
