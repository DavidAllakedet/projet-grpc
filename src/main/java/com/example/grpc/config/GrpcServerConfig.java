package com.example.grpc.config;

import com.example.grpc.server.ProductGrpcService;
import com.example.grpc.service.ProductService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int port;

    private Server server;
    private final ProductService productService;

    public GrpcServerConfig(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void start() throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new ProductGrpcService(productService))
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();
        System.out.println("=== Serveur gRPC démarré sur le port " + port + " ===");
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
            System.out.println("=== Serveur gRPC arrêté ===");
        }
    }
}
