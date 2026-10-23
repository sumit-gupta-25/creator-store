package com.sumit.creatorstore.services;

import com.sumit.creatorstore.dto.OrderItemRequest;
import com.sumit.creatorstore.dto.OrderRequest;
import com.sumit.creatorstore.entities.Order;
import com.sumit.creatorstore.entities.OrderItem;
import com.sumit.creatorstore.entities.Product;
import com.sumit.creatorstore.exceptions.InsufficientStockException;
import com.sumit.creatorstore.exceptions.ResourceNotFoundException;
import com.sumit.creatorstore.repositories.OrderRepository;
import com.sumit.creatorstore.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
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

            // Check product stock
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product ID "
                                + itemRequest.getProductId()
                );
            }

            // Calculate item price using the current database price
            BigDecimal priceOfItem = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(priceOfItem);

            // Decrease available stock
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

        return orderRepository.save(order);
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id " + id
                ));
    }
}
