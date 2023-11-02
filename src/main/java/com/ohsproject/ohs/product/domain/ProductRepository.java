package com.ohsproject.ohs.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update Product p set p.stock = p.stock - :orderQty where p.id = :productId and p.stock >= :orderQty")
    int decreaseProductStock(Long productId, int orderQty);
}
