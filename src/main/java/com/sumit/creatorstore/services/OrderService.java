package com.sumit.creatorstore.services;

import com.sumit.creatorstore.dto.OrderItemRequest;
import com.sumit.creatorstore.dto.OrderItemResponse;
import com.sumit.creatorstore.dto.OrderRequest;
import com.sumit.creatorstore.dto.OrderResponse;
import com.sumit.creatorstore.entities.Order;
import com.sumit.creatorstore.entities.OrderItem;
import com.sumit.creatorstore.entities.Product;
import com.sumit.creatorstore.exceptions.InsufficientStockException;
import com.sumit.creatorstore.exceptions.ResourceNotFoundException;
import com.sumit.creatorstore.repositories.OrderRepository;
import com.sumit.creatorstore.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        Order order = Order.builder()
                .customerName(orderRequest.getCustomerName())
                .customerEmail(orderRequest.getCustomerEmail())
                .status("CONFIRMED")
                .build();

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(
                    itemRequest.getProductId()
            ).orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with id "
                            + itemRequest.getProductId()
            ));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product ID "
                                + itemRequest.getProductId()
                );
            }

            BigDecimal priceOfItem = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(priceOfItem);

            product.setStockQuantity(
                    product.getStockQuantity() - itemRequest.getQuantity()
            );

            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toOrderResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id " + id
                ));

        return toOrderResponse(order);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase().multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalPrice(),
                items,
                order.getCreatedAt()
        );
    }
}
