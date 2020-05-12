package com.wiredbrain.wiredbrainapi.handler;

import com.wiredbrain.wiredbrainapi.model.Product;
import com.wiredbrain.wiredbrainapi.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {

    private ProductRepository productRepository;

    public ProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest serverRequest) {

        Flux<Product> allProducts = productRepository.findAll();

//        ServerResponse serverResponse = allProducts
//                .flatMap(products -> ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(products, Product.class));


        /*return productRepository.findAll()
                .collectList()
                .flatMap(it -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(it)));*/

        return ServerResponse.ok().body(allProducts, Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest serverRequest) {

        return productRepository.findById(serverRequest.pathVariable("id"))
                .flatMap(product -> {
                    return ServerResponse.ok().body(fromObject(product));
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Product.class)
                .flatMap(product -> {
                    return productRepository.save(product);
                })
                .flatMap(savedProduct -> {
                    return ServerResponse.status(HttpStatus.CREATED)
                            .body(fromObject(savedProduct));
                });
    }

    public Mono<ServerResponse> updateProduct(ServerRequest serverRequest) {

        Mono<Product> existingProduct = productRepository.findById(serverRequest.pathVariable("id"));
        Mono<Product> updateProductRequest = serverRequest.bodyToMono(Product.class);

        return Mono.zip(
                existingProduct,
                updateProductRequest,
                (savedProduct, updateRequest) -> {
                    return productRepository.save(
                            new Product(savedProduct.getId(), updateRequest.getName(), updateRequest.getPrice())
                    );

                })
                .flatMap(it -> ServerResponse.ok().body(it, Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {

        return productRepository
                .findById(serverRequest.pathVariable("id"))
                .flatMap(product -> productRepository.delete(product))
                .flatMap(deletedProduct -> ServerResponse.ok().body(fromObject(deletedProduct)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteAllProducts(ServerRequest serverRequest) {

        return ServerResponse.ok().build(productRepository.deleteAll());
    }
}