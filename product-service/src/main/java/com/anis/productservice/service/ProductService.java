package com.anis.productservice.service;

import com.anis.productservice.dto.ProductDto;
import com.anis.productservice.event.ProductViewEvent;
import com.anis.productservice.repository.ProductRepository;
import com.anis.productservice.util.EntityDtoUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductViewEventProducer productViewEventProducer;

    public Mono<ProductDto> getProduct(int id) {
        return this.repository.findById(id)
                .doOnNext(e -> this.productViewEventProducer.emitEvent(new ProductViewEvent((e.getId()))))
                .map(EntityDtoUtil::toDto);
    }
}
