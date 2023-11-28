package com.ohsproject.ohs.order.service;

import com.ohsproject.ohs.global.redis.PaymentProduct;
import com.ohsproject.ohs.global.redis.PaymentProductOperation;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import com.ohsproject.ohs.order.domain.*;
import com.ohsproject.ohs.order.dto.request.OrderCompleteRequest;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.dto.request.OrderDetailRequest;
import com.ohsproject.ohs.order.exception.OrderNotFoundException;
import com.ohsproject.ohs.product.domain.Product;
import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.exception.InsufficientStockException;
import com.ohsproject.ohs.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentProductOperation paymentProductOperation;

    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        PaymentProductOperation paymentProductOperation) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.paymentProductOperation = paymentProductOperation;
    }

    @Transactional
    public Long placeOrder(OrderCreateRequest orderCreateRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        final List<OrderDetailRequest> orderDetailRequests = orderCreateRequest.getOrderDetailRequests();

        validateProductStocks(orderDetailRequests);
        createPaymentProducts(orderDetailRequests, memberId);

        Order order = orderCreateRequest.toOrder(member);
        Long orderId = orderRepository.save(order).getId();
        createOrderDetails(orderDetailRequests, order);

        return orderId;
    }

    private void validateProductStocks(List<OrderDetailRequest> orderDetailRequests) {
        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            Product product = productRepository.findById(orderDetailRequest.getProductId())
                    .orElseThrow(ProductNotFoundException::new);
            Long paymentProductQty = paymentProductOperation.count(product.getId());
            product.checkSpareStock(orderDetailRequest.getQty(), paymentProductQty);
        }
    }

    private void createPaymentProducts(List<OrderDetailRequest> orderDetailRequests, Long memberId) {
        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            PaymentProduct paymentProduct = PaymentProduct.builder()
                    .productId(orderDetailRequest.getProductId())
                    .memberId(memberId)
                    .qty(orderDetailRequest.getQty())
                    .build();
            paymentProductOperation.add(paymentProduct);
        }
    }

    private void createOrderDetails(List<OrderDetailRequest> orderDetailRequests, Order order) {
        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            Product product = productRepository.findById(orderDetailRequest.getProductId())
                    .orElseThrow(ProductNotFoundException::new);

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .qty(orderDetailRequest.getQty())
                    .build();
            orderDetailRepository.save(orderDetail);
        }
    }

    @Transactional
    public void completeOrder(OrderCompleteRequest orderCompleteRequest, Long orderId, Long memberId) {
        final List<OrderDetailRequest> orderDetailRequests = orderCompleteRequest.getOrderDetailRequests();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        order.validateOrder();
        order.changeStatus(OrderStatus.ORDER_COMPLETE);

        removePaymentStocks(orderDetailRequests, memberId);
        updateProductStocks(orderDetailRequests);
    }

    private void removePaymentStocks(List<OrderDetailRequest> orderDetailRequests, Long memberId) {
        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            PaymentProduct paymentProduct = PaymentProduct.builder()
                    .productId(orderDetailRequest.getProductId())
                    .memberId(memberId)
                    .qty(orderDetailRequest.getQty())
                    .build();
            paymentProductOperation.remove(paymentProduct);
        }
    }

    private void updateProductStocks(List<OrderDetailRequest> orderDetailRequests) {
        orderDetailRequests.sort(Comparator.comparing(OrderDetailRequest::getProductId));

        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            int affectedRowCount = productRepository.decreaseProductStock(orderDetailRequest.getProductId(), orderDetailRequest.getQty());
            if (affectedRowCount == 0) {
                throw new InsufficientStockException();
            }
        }
    }
}
