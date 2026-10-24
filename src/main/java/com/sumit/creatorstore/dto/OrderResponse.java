package com.sumit.creatorstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        String status,
        BigDecimal totalPrice,
        List<OrderItemResponse> orderItems,
        LocalDateTime createdAt
) {}
