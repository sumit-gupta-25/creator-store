package com.sumit.creatorstore.repositories;

import com.sumit.creatorstore.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
