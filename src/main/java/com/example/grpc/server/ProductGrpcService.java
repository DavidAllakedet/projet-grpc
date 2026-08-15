package com.example.grpc.server;

import com.example.grpc.model.Product;
import com.example.grpc.proto.*;
import com.example.grpc.service.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = productService.getProductById(request.getId())
                .orElse(null);

        if (product == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product not found with id: " + request.getId())
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(toProductResponse(product));
        responseObserver.onCompleted();
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        List<Product> products;

        if (!request.getCategory().isEmpty()) {
            products = productService.getProductsByCategory(request.getCategory());
        } else if (request.hasInStock()) {
            products = productService.getProductsByStock(request.getInStock());
        } else {
            products = productService.getAllProducts();
        }

        ListProductsResponse.Builder response = ListProductsResponse.newBuilder();
        products.forEach(p -> response.addProducts(toProductResponse(p)));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.getInStock()
        );

        Product saved = productService.createProduct(product);
        responseObserver.onNext(toProductResponse(saved));
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.getInStock()
        );

        try {
            Product updated = productService.updateProduct(request.getId(), product);
            responseObserver.onNext(toProductResponse(updated));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product not found with id: " + request.getId())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        boolean deleted = productService.deleteProduct(request.getId());

        if (deleted) {
            responseObserver.onNext(DeleteProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted successfully")
                    .build());
        } else {
            responseObserver.onNext(DeleteProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Product not found with id: " + request.getId())
                    .build());
        }
        responseObserver.onCompleted();
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription() != null ? product.getDescription() : "")
                .setPrice(product.getPrice())
                .setCategory(product.getCategory())
                .setInStock(product.getInStock())
                .build();
    }
}
