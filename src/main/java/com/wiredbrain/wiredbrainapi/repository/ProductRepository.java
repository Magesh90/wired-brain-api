package com.wiredbrain.wiredbrainapi.repository;

import com.wiredbrain.wiredbrainapi.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository
        extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findByNameOrderByPrice(String name);

}
