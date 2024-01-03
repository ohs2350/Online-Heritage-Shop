package com.ohsproject.ohs.product.controller;

import com.ohsproject.ohs.product.domain.SortType;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToSortConverter implements Converter<String, SortType> {

    @Override
    public SortType convert(String source) {
        return SortType.of(source);
    }
}
