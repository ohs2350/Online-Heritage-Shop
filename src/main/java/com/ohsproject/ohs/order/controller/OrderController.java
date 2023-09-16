package com.ohsproject.ohs.order.controller;

import com.ohsproject.ohs.order.domain.Order;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid final OrderCreateRequest orderCreateRequest) {
        Long id = orderService.placeOrder(orderCreateRequest);

        return ResponseEntity.created(URI.create("/api/v1/order/" + id)).build();
    }
}
