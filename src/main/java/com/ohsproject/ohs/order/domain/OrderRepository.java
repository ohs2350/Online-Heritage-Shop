package com.ohsproject.ohs.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update Order o set o.status = :newStatus where o.orderDate <= :validOrderDate and o.status = :oldStatus")
    void updateUnpaidOrderStatus(@Param("oldStatus") OrderStatus oldStatus,
                                 @Param("newStatus") OrderStatus newStatus,
                                 @Param("validOrderDate") LocalDateTime validOrderDate);
}
