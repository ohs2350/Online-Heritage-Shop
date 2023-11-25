package com.ohsproject.ohs.order.controller;

import com.ohsproject.ohs.global.annotation.Login;
import com.ohsproject.ohs.global.annotation.CurrentMember;
import com.ohsproject.ohs.member.dto.request.MemberInfo;
import com.ohsproject.ohs.order.dto.request.OrderCompleteRequest;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Login
    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody @Valid final OrderCreateRequest orderCreateRequest,
                                           @CurrentMember final MemberInfo memberInfo) {
        Long id = orderService.placeOrder(orderCreateRequest, memberInfo.getId());

        return ResponseEntity.created(URI.create("/api/v1/order/" + id)).build();
    }

    @Login
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> completeOrder(@RequestBody @Valid final OrderCompleteRequest orderCompleteRequest,
                              @PathVariable final Long orderId,
                              @CurrentMember final MemberInfo memberInfo) {
        orderService.completeOrder(orderCompleteRequest, orderId, memberInfo.getId());

        return ResponseEntity.ok().build();
    }
}
