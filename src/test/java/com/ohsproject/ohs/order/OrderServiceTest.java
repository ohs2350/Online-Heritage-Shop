package com.ohsproject.ohs.order;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.order.domain.Order;
import com.ohsproject.ohs.order.domain.OrderRepository;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.service.OrderService;
import com.ohsproject.ohs.orderDetail.domain.OrderDetail;
import com.ohsproject.ohs.orderDetail.domain.OrderDetailRepository;
import com.ohsproject.ohs.product.domain.Product;
import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.exception.InsufficientStockException;
import com.ohsproject.ohs.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    private static final int QTY_LESS_THAN_STOCK = 1;

    private static final int QTY_MORE_THAN_STOCK = 100;

    @Test
    @DisplayName("주문에 성공하는 경우")
    void testPlaceOrder() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Product product = createSampleProduct();
        Member member = createSampleMember();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(createSampleOrder());
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(createSampleOrderDetail());

        // when
        Long orderId = orderService.placeOrder(orderCreateRequest);

        // then
        assertAll(
                () -> assertThat(orderId).isEqualTo(1L),
                () -> verify(memberRepository, times(1)).findById(member.getId()),
                () -> verify(productRepository, times(1)).findById(product.getId()),
                () -> verify(productRepository, times(1)).save(any(Product.class)),
                () -> verify(orderRepository, times(1)).save(any(Order.class)),
                () -> verify(orderDetailRepository, times(1)).save(any(OrderDetail.class))
        );
    }

    @Test
    @DisplayName("잘못된 상품번호로 주문하는 경우")
    public void testPlaceOrder_ProductNotFound() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Member member = createSampleMember();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> orderService.placeOrder(orderCreateRequest));
    }

    @Test
    @DisplayName("재고량이 부족한 경우")
    public void testPlaceOrder_InsufficientStock() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_MORE_THAN_STOCK);
        Product product = createSampleProduct();
        Member member = createSampleMember();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // when, then
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(orderCreateRequest));
    }

    private OrderCreateRequest createSampleOrderCreateRequest(int qty) {
        List<Long> productIds = List.of(1L);
        List<Integer> quantities = List.of(qty);
        return new OrderCreateRequest(1L, productIds, quantities, 1000);
    }

    private Product createSampleProduct() {
        return Product.builder()
                .id(1L)
                .price(1000)
                .stock(2)
                .build();
    }

    private Member createSampleMember() {
        return new Member(1L, "testMember");
    }

    private Order createSampleOrder() {
        return Order.builder()
                .id(1L)
                .build();
    }

    private OrderDetail createSampleOrderDetail() {
        return  OrderDetail.builder().build();
    }
}
