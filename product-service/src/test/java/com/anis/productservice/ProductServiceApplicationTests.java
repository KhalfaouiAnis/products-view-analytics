package com.anis.productservice;

import com.anis.productservice.event.ProductViewEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient
class ProductServiceApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Test
    void productViewEventTest() {

        // view products
        viewProductSuccess(1);
        viewProductSuccess(1);
        viewProductError(5200);
        viewProductSuccess(3);

        // check if the events are emitted
        Flux<ReceiverRecord<String, ProductViewEvent>> flux = this.<ProductViewEvent>createReceiver(PRODUCT_VIEW_EVENTS)
                .receive()
                .take(3);

        StepVerifier.create(flux)
                .consumeNextWith(r -> Assertions.assertEquals(1, r.value().getProductId()))
                .consumeNextWith(r -> Assertions.assertEquals(1, r.value().getProductId()))
                .consumeNextWith(r -> Assertions.assertEquals(3, r.value().getProductId()))
                .verifyComplete();
    }

    private void viewProductSuccess(int id) {
        this.client
                .get()
                .uri("/product/" + id)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.description").isEqualTo("product-" + id);
    }

    private void viewProductError(int id) {
        this.client
                .get()
                .uri("/product/" + id)
                .exchange()
                .expectStatus().is4xxClientError();
    }

}
