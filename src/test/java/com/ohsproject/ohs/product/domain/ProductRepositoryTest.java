package com.ohsproject.ohs.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.ohsproject.ohs.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("주문한 상품 수량만큼 재고량이 감소한다. ")
    public void decreaseProductStock() {
        // given
        Product product = createSampleProduct();
        productRepository.save(product);

        Long productId = product.getId();
        int orderQty = product.getStock() - 1;

        // when
        int affectedRowCount = productRepository.decreaseProductStock(productId, orderQty);

        // then
        Product updatedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(product.getStock() - orderQty, updatedProduct.getStock());
        assertEquals(1, affectedRowCount);
    }

    @Test
    @DisplayName("재고량이 부족한 경우 수정하지 않는다. 따라서 영향받은 로우가 없다")
    public void failDecreaseProductStock() {
        // given
        Product product = createSampleProduct();
        productRepository.save(product);

        Long productId = product.getId();
        int orderQty = product.getStock() + 1;

        // when
        int affectedRowCount = productRepository.decreaseProductStock(productId, orderQty);

        // then
        Product updatedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(product.getStock(), updatedProduct.getStock());
        assertEquals(0, affectedRowCount);
    }

    private Product createSampleProduct() {
        return Product.builder()
                .id(PRODUCT_ID_1ST)
                .stock(PRODUCT_STOCK_1ST)
                .build();
    }
}
