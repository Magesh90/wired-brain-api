package com.wiredbrain.wiredbrainapi;

import com.wiredbrain.wiredbrainapi.model.Product;
import com.wiredbrain.wiredbrainapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestAPIBindingToServer {

    @LocalServerPort
    String port;

    private WebTestClient webTestClient;

    private List<Product> expectedProducts;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void beforeEach() {

        this.expectedProducts = productRepository.findAll().collectList().block();
        this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port + "/products")
                .build();
    }

    @Test
    void testAllProducts(){
        webTestClient
                .get()
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedProducts);
    }

}
