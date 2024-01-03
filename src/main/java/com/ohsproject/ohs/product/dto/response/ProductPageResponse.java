package com.ohsproject.ohs.product.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductPageResponse {
    private final List<ProductResponse> productResponses;

    private final int totalPages;

    public ProductPageResponse(List<ProductResponse> productResponses, int totalPages) {
        this.productResponses = productResponses;
        this.totalPages = totalPages;
    }
}
