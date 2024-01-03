package com.ohsproject.ohs.product.domain;

import com.ohsproject.ohs.global.config.QueryDslConfig;
import com.ohsproject.ohs.product.dto.ProductDto;
import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.ohsproject.ohs.support.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryProductRepository categoryProductRepository;

    @Test
    @DisplayName("주문한 상품 수량만큼 재고량이 감소한다.")
    void decreaseProductStock() {
        // given
        Product product = createProduct();
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
    @DisplayName("재고량이 부족한 경우 수정하지 않는다.")
    void failDecreaseProductStock() {
        // given
        Product product = createProduct();
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

    @Test
    @DisplayName("카테고리 번호에 맞는 상품 목록을 조회한다.")
    void findAllByCategoryCondition() {
        // given
        insertSampleData();
        ProductSearchRequest request = new ProductSearchRequest(2L, 1, 1, SortType.PRICE_ASC, null, null);

        // when
        Page<ProductDto> page = productRepository.findAllByConditions(request);

        // then
        assertAll(
                () -> assertThat(page).isNotNull(),
                () -> assertThat(page.getContent()).isNotEmpty(),
                () -> assertThat(page.getContent().size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("가격 범위에 맞는 상품 목록을 조회한다.")
    void findAllByPriceRangeCondition() {
        // given
        insertSampleData();
        ProductSearchRequest request = new ProductSearchRequest(1L, 1, 1, SortType.PRICE_ASC, 0, 3000);

        // when
        Page<ProductDto> page = productRepository.findAllByConditions(request);

        // then
        assertAll(
                () -> assertThat(page).isNotNull(),
                () -> assertThat(page.getContent()).isNotEmpty(),
                () -> assertThat(page.getContent().size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("상품 목록은 정렬 조건에 맞게 정렬된다.")
    void findAllByConditionsWithPriceOrdering() {
        // given
        insertSampleData();
        ProductSearchRequest request = new ProductSearchRequest(1L, 1, 10, SortType.PRICE_ASC, null, null);

        // when
        Page<ProductDto> page = productRepository.findAllByConditions(request);

        // then
        List<ProductDto> list = page.getContent();
        assertAll(
                () -> assertThat(page).isNotNull(),
                () -> assertThat(list).isNotEmpty(),
                () -> assertTrue(list.get(0).getPrice() <= list.get(1).getPrice())
        );
    }

    private void insertSampleData() {
        Product product1 = createProduct(1L, 10, 3000);
        Product product2 = createProduct(2L, 10, 3000);
        Product product3 = createProduct(3L, 10, 10000);
        Product product4 = createProduct(4L, 10, 5000);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);

        Category category1 = Category.builder().id(1L).build();
        Category category2 = Category.builder().id(2L).build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        CategoryProduct categoryProduct1 = CategoryProduct.builder()
                .id(1L)
                .category(category1)
                .product(product1)
                .build();
        CategoryProduct categoryProduct2 = CategoryProduct.builder()
                .id(2L)
                .category(category2)
                .product(product2)
                .build();
        CategoryProduct categoryProduct3 = CategoryProduct.builder()
                .id(3L)
                .category(category1)
                .product(product3)
                .build();
        CategoryProduct categoryProduct4 = CategoryProduct.builder()
                .id(4L)
                .category(category1)
                .product(product4)
                .build();
        categoryProductRepository.save(categoryProduct1);
        categoryProductRepository.save(categoryProduct2);
        categoryProductRepository.save(categoryProduct3);
        categoryProductRepository.save(categoryProduct4);
    }
}
