package com.ohsproject.ohs.product.domain;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

public enum SortType {
    PRICE_ASC("priceAsc", "price", ASC),
    PRICE_DESC("priceDesc", "price", DESC),
    POPULARITY_DESC("popularityDesc", "id", DESC),
    LATEST_ASC("latestAsc", "id", DESC);

    private final String viewValue;
    private final String property;
    private final Direction direction;

    SortType(String viewValue, String property, Direction direction) {
        this.viewValue = viewValue;
        this.property = property;
        this.direction = direction;
    }

    public String getViewValue() {
        return viewValue;
    }

    public Order makeSortOrder() {
        return new Order(direction, property);
    }

    public static SortType of(String viewValue) {
        for (SortType enumValue : SortType.values()) {
            if (enumValue.getViewValue().equals(viewValue)) {
                return enumValue;
            }
        }
        return POPULARITY_DESC;
    }
}