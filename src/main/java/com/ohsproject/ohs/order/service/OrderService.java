package com.ohsproject.ohs.order.service;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import com.ohsproject.ohs.order.domain.Order;
import com.ohsproject.ohs.order.domain.OrderRepository;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.orderDetail.domain.OrderDetail;
import com.ohsproject.ohs.orderDetail.domain.OrderDetailRepository;
import com.ohsproject.ohs.product.domain.Product;
import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.exception.InsufficientStockException;
import com.ohsproject.ohs.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        MemberRepository memberRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Long placeOrder(OrderCreateRequest orderCreateRequest) {
        Order order = createOrder(orderCreateRequest);
        createOrderDetails(order, orderCreateRequest);

        return orderRepository.save(order).getId();
    }

    private Order createOrder(OrderCreateRequest orderCreateRequest) {
        Member member = findMemberById(orderCreateRequest.getMemberId());
        return orderCreateRequest.toOrder(member);
    }

    private void createOrderDetails (Order order, OrderCreateRequest orderCreateRequest) {
        List<Long> productIds = orderCreateRequest.getProductIds();
        List<Integer> quantities = orderCreateRequest.getQuantities();

        IntStream.range(0, productIds.size())
                .forEach(index -> {
                    Long productId = productIds.get(index);
                    Integer quantity = quantities.get(index);

                    Product product = findProductById(productId);
                    updateProductStock(product, quantity);
                    validateProductStock(product);

                    OrderDetail orderDetail = createOrderDetail(order, product, quantity);
                    orderDetailRepository.save(orderDetail);
                });
    }

    private OrderDetail createOrderDetail(Order order, Product product, int quantity) {
        return OrderDetail.builder()
                .qty(quantity)
                .product(product)
                .order(order)
                .build();
    }

    private void updateProductStock(Product product, int qty) {
        product.decreaseStock(qty);
        productRepository.save(product);
    }

    private void validateProductStock(Product product) {
        if (product.getStock() < 0) {
            throw new InsufficientStockException();
        }
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

}
