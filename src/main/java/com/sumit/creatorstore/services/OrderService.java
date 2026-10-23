package com.sumit.creatorstore.services;

import com.sumit.creatorstore.dto.OrderItemRequest;
import com.sumit.creatorstore.dto.OrderRequest;
import com.sumit.creatorstore.entities.Order;
import com.sumit.creatorstore.entities.OrderItem;
import com.sumit.creatorstore.entities.Product;
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
            ).orElseThrow(() -> new RuntimeException(
                    "Product not found with id " + itemRequest.getProductId()
            ));

            // Check the product stock
            if(product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for " + itemRequest.getProductId());
            }

            // Calculate total price
            BigDecimal priceOfItem = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(priceOfItem);

            // Update the product table with latest stock quantity
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
}
