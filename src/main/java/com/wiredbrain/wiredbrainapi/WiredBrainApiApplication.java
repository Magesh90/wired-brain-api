package com.wiredbrain.wiredbrainapi;

import com.wiredbrain.wiredbrainapi.handler.ProductHandler;
import com.wiredbrain.wiredbrainapi.model.Product;
import com.wiredbrain.wiredbrainapi.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class WiredBrainApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiredBrainApiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> {

            Flux<Product> products = Flux.just(
                    new Product(null, "Filter Coffee", 50.00),
                    new Product(null, "Dalgona Coffee", 150.00),
                    new Product(null, "Hot Chocolate", 250.00))
                    .flatMap(productRepository::save);

            products
                    .thenMany(productRepository.findAll())
                    .subscribe(System.out::println);

        };
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction(ProductHandler productHandler) {
        return route(GET("/v1/products"), productHandler::getAllProducts)
                .andRoute(POST("/v1/products"), productHandler::saveProduct)
                .andRoute(DELETE("/v1/products"), productHandler::deleteAllProducts)
                .andRoute(DELETE("/v1/products/{id}"), productHandler::deleteProduct)
                .andRoute(PUT("/v1/product/{id}"), productHandler::updateProduct)
                .andRoute(GET("/v1/product/{id}"), productHandler::getProduct);
    }
}
