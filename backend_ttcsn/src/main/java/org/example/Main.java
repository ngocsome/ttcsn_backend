package org.example;

import io.javalin.Javalin;
import org.example.controller.GeneticController;
import org.example.service.GeneticAlgorithmService;
import org.example.service.GeneticAlgorithmServiceImpl;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            // Cho phép FE gọi khác port (CORS)
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        });

        // API test đơn giản
        app.get("/ping", ctx -> ctx.result("pong"));

        // Khởi tạo service & controller
        GeneticAlgorithmService service = new GeneticAlgorithmServiceImpl();
        new GeneticController(app, service);

        app.start(7000); // http://localhost:7000
        System.out.println("🚀 Server started on http://localhost:7000");
    }
}
