package com.example.grpc.config;

import com.example.grpc.server.ProductGrpcService;
import com.example.grpc.service.ProductService;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import io.grpc.protobuf.services.ProtoReflectionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

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
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream is = new ClassPathResource("grpc-server.p12").getInputStream()) {
            ks.load(is, "changeit".toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "changeit".toCharArray());

        io.grpc.netty.shaded.io.netty.handler.ssl.SslContext sslContext = GrpcSslContexts.configure(
                SslContextBuilder.forServer(kmf)
                        .sslProvider(SslProvider.JDK)
        ).build();

        server = NettyServerBuilder.forPort(port)
                .sslContext(sslContext)
                .addService(new ProductGrpcService(productService))
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();
        System.out.println("=== Serveur gRPC TLS demarre sur le port " + port + " ===");
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
            System.out.println("=== Serveur gRPC arrete ===");
        }
    }
}
