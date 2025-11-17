package org.example;

import io.javalin.Javalin;
import org.example.controller.GeneticController;
import org.example.service.GeneticAlgorithmService;
import org.example.service.GeneticAlgorithmServiceImpl;

public class Main {

    public static void main(String[] args) {

        // Khởi tạo service GA
        GeneticAlgorithmService service = new GeneticAlgorithmServiceImpl();

        // Tạo app Javalin
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.routing.ignoreTrailingSlashes = true;
        });

        // Endpoint test
        app.get("/api/health", ctx -> ctx.result("OK"));

        // Đăng ký controller GA
        new GeneticController(app, service);

        // Chạy server
        app.start(7000);

        System.out.println("🚀 Backend running at http://localhost:7000");
    }
}
