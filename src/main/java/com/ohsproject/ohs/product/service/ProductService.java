package com.ohsproject.ohs.product.service;

import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.dto.ProductDto;
import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import com.ohsproject.ohs.product.dto.response.ProductPageResponse;
import com.ohsproject.ohs.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ProductPageResponse getList(ProductSearchRequest productSearchRequest) {
        Page<ProductDto> products = productRepository.findAllByConditions(productSearchRequest);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return new ProductPageResponse(productResponses, products.getTotalPages());
    }
}