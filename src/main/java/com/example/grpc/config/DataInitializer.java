package com.example.grpc.config;

import com.example.grpc.model.Product;
import com.example.grpc.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        productRepository.save(new Product(
                "Ordinateur Portable",
                "Laptop 15 pouces, 16GB RAM, 512GB SSD",
                899.99,
                "Informatique",
                true
        ));
        productRepository.save(new Product(
                "Smartphone",
                "Smartphone 6.5 pouces, 128GB stockage",
                499.99,
                "Téléphonie",
                true
        ));
        productRepository.save(new Product(
                "Casque Audio",
                "Casque sans fil avec réduction de bruit",
                149.99,
                "Audio",
                true
        ));
        productRepository.save(new Product(
                "Tablette",
                "Tablette 10 pouces, 64GB, avec stylet",
                329.99,
                "Informatique",
                false
        ));
        productRepository.save(new Product(
                "Clavier Mécanique",
                "Clavier RGB switch bleu",
                79.99,
                "Périphériques",
                true
        ));
        productRepository.save(new Product(
                "Souris Gaming",
                "Souris ergonomique 16000 DPI",
                59.99,
                "Périphériques",
                true
        ));
        System.out.println("=== Données initiales chargées : 6 produits ===");
    }
}
