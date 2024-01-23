package com.anis.productservice.config;

import com.anis.productservice.event.ProductViewEvent;
import com.anis.productservice.service.ProductViewEventProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SenderOptions<String, ProductViewEvent> senderOptions(KafkaProperties properties){
        return SenderOptions.create(properties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, ProductViewEvent> producerTemplate(SenderOptions<String, ProductViewEvent> senderOptions){
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ProductViewEventProducer productViewEventProducer(ReactiveKafkaProducerTemplate<String, ProductViewEvent> template){
        Sinks.Many<ProductViewEvent> sink = Sinks.many().unicast().<ProductViewEvent>onBackpressureBuffer();
        Flux<ProductViewEvent> flux = sink.asFlux();
        ProductViewEventProducer eventProducer = new ProductViewEventProducer(template, sink, flux, "product-view-events");
        eventProducer.subscribe();
        return eventProducer;
    }

}
