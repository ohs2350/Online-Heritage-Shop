package com.ohsproject.ohs.product.domain;

import com.ohsproject.ohs.product.dto.ProductDto;
import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ohsproject.ohs.product.domain.QCategory.category;
import static com.ohsproject.ohs.product.domain.QCategoryProduct.categoryProduct;
import static com.ohsproject.ohs.product.domain.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<ProductDto> findAllByConditions(ProductSearchRequest productSearchRequest) {
        List<ProductDto> productList =  jpaQueryFactory
                .select(
                        Projections.constructor(
                                ProductDto.class,
                                product.id,
                                product.name,
                                product.price,
                                product.stock
                        )
                )
                .from(product)
                .innerJoin(categoryProduct).on(product.id.eq(categoryProduct.product.id))
                .innerJoin(category).on(category.id.eq(categoryProduct.category.id))
                .where(
                        eqCategoryId(productSearchRequest.getCategory()),
                        minPriceGoe(productSearchRequest.getMinPrice()),
                        maxPriceLoe(productSearchRequest.getMaxPrice())
                )
                .orderBy(makeOrderSpecifier(product, productSearchRequest.getSort().makeSortOrder()))
                .offset(productSearchRequest.getOffset())
                .limit(productSearchRequest.getListSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(product.count())
                .from(product);

        return PageableExecutionUtils.getPage(productList, toPageable(productSearchRequest), countQuery::fetchOne);
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        return category.id.eq(categoryId);
    }

    private BooleanExpression minPriceGoe(Integer minPrice) {
        if (minPrice != null) {
            return QProduct.product.price.goe(minPrice);
        }
        return null;
    }

    private BooleanExpression maxPriceLoe(Integer maxPrice) {
        if (maxPrice != null) {
            return QProduct.product.price.loe(maxPrice);
        }
        return null;
    }

    private <T> OrderSpecifier<?> makeOrderSpecifier(EntityPathBase<T> qClass, Sort.Order sortOrder) {
        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        PathBuilder<T> pathBuilder = new PathBuilder<>(qClass.getType(), qClass.getMetadata());

        return new OrderSpecifier(direction, pathBuilder.get(sortOrder.getProperty()));
    }

    private Pageable toPageable(ProductSearchRequest productSearchRequest) {
        int page = productSearchRequest.getPage();
        int size = productSearchRequest.getListSize();
        Sort.Order order = productSearchRequest.getSort().makeSortOrder();

        return PageRequest.of(page, size, order.getDirection(), order.getProperty());
    }
}
