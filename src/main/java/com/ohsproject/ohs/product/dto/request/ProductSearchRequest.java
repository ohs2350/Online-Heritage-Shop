package com.ohsproject.ohs.product.dto.request;

import com.ohsproject.ohs.product.domain.SortType;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class ProductSearchRequest {

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long category;

    @Positive(message = "잘못된 요청입니다.")
    private Integer page;

    @Positive(message = "잘못된 요청입니다.")
    private Integer listSize;

    private SortType sort;

    @PositiveOrZero(message = "잘못된 요청입니다.")
    private Integer minPrice;

    @PositiveOrZero(message = "잘못된 요청입니다.")
    private Integer maxPrice;

    private ProductSearchRequest() {
    }

    public ProductSearchRequest(Long category, Integer page, Integer listSize, SortType sort, Integer minPrice, Integer maxPrice) {
        this.category = category;
        this.page = page != null ? page : 1;
        this.listSize = listSize;
        this.sort = sort != null ? sort : SortType.POPULARITY_DESC;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;

        checkPriceRange();
    }

    private void checkPriceRange() {
        if (this.minPrice != null && this.maxPrice != null) {
            minPrice = Math.min(maxPrice, minPrice);
            maxPrice = Math.max(maxPrice, minPrice);
        }
    }

    public long getOffset() {
        return (long) (page-1) * listSize;
    }
}