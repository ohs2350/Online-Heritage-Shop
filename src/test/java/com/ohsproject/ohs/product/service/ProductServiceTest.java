package com.ohsproject.ohs.product.service;

import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.domain.SortType;
import com.ohsproject.ohs.product.dto.ProductDto;
import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import com.ohsproject.ohs.product.dto.response.ProductPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 목록 조회에 성공하는 경우")
    void getList() {
        // given
        ProductDto productDto = new ProductDto(null, null, 0, 0);
        Pageable pageable = PageRequest.of(0, 2);
        ProductSearchRequest productSearchRequest = new ProductSearchRequest(1L, 1, 10, SortType.PRICE_ASC, 0, 1000);

        when(productRepository.findAllByConditions(productSearchRequest)).thenReturn(new PageImpl<>(List.of(productDto), pageable, 1));

        // when
        ProductPageResponse productPageResponse = productService.getList(productSearchRequest);

        // then
        assertAll(
                () -> verify(productRepository).findAllByConditions(productSearchRequest),
                () -> assertThat(productPageResponse).isNotNull()
        );
    }
}
