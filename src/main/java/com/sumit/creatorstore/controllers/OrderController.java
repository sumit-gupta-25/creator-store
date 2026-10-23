package com.sumit.creatorstore.controllers;

import com.sumit.creatorstore.dto.OrderRequest;
import com.sumit.creatorstore.entities.Order;
import com.sumit.creatorstore.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}
