package com.ohsproject.ohs.product.domain;

import com.ohsproject.ohs.product.dto.ProductDto;
import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {
    Page<ProductDto> findAllByConditions(ProductSearchRequest productSearchRequest);
}
