package com.ohsproject.ohs.order.service;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.order.domain.*;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.dto.request.OrderDetailRequest;
import com.ohsproject.ohs.order.exception.OrderNotValidException;
import com.ohsproject.ohs.global.redis.PaymentProduct;
import com.ohsproject.ohs.global.redis.PaymentProductOperation;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ohsproject.ohs.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


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

    @Mock
    private PaymentProductOperation paymentProductOperation;

    @Test
    @DisplayName("주문에 성공하는 경우 - 단일 상품")
    void testPlaceOrder() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Product product = createSampleProduct(PRODUCT_ID_1ST, PRODUCT_STOCK_1ST);
        Member member = createSampleMember();
        Order order = createSampleOrder();

        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentProductOperation.count(anyLong())).thenReturn(0L);

        // when
        Long orderId = orderService.placeOrder(orderCreateRequest, MEMBER_ID);

        // then
        assertAll(
                () -> assertThat(orderId).isNotNull(),
                () -> verify(memberRepository, times(1)).findById(member.getId()),
                () -> verify(orderRepository, times(1)).save(any(Order.class)),
                () -> verify(orderDetailRepository, times(1)).save(any(OrderDetail.class))
        );
    }

    @Test
    @DisplayName("주문에 성공하는 경우 - 다중 상품")
    void testPlaceOrder_MultiProduct() {
        // given
        OrderCreateRequest orderCreateRequest = createMultiProductOrderCreateRequest();
        Product product1 = createSampleProduct(PRODUCT_ID_1ST, PRODUCT_STOCK_1ST);
        Product product2 = createSampleProduct(PRODUCT_ID_2ND, PRODUCT_STOCK_2ND);
        Member member = createSampleMember();
        Order saveOrder = createSampleOrder();
        int count = orderCreateRequest.getOrderDetailRequests().size();

        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(productRepository.findById(PRODUCT_ID_1ST)).thenReturn(Optional.of(product1));
        when(productRepository.findById(PRODUCT_ID_2ND)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);
        when(paymentProductOperation.count(anyLong())).thenReturn(0L);

        // when
        Long orderId = orderService.placeOrder(orderCreateRequest, MEMBER_ID);

        // then
        assertAll(
                () -> assertThat(orderId).isNotNull(),
                () -> verify(memberRepository, times(1)).findById(member.getId()),
                () -> verify(orderRepository, times(1)).save(any(Order.class)),
                () -> verify(productRepository, times(2)).findById(product1.getId()),
                () -> verify(productRepository, times(2)).findById(product2.getId()),
                () -> verify(orderDetailRepository, times(count)).save(any(OrderDetail.class))
        );
    }

    @Test
    @DisplayName("잘못된 상품번호로 주문하는 경우 예외 발생")
    public void testPlaceOrder_ProductNotFound() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Member member = createSampleMember();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> orderService.placeOrder(orderCreateRequest, MEMBER_ID));
    }

    @Test
    @DisplayName("주문한 상품의 재고량이 부족한 경우 예외 발생")
    public void testPlaceOrder_InsufficientStock() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_MORE_THAN_STOCK);
        Product product = createSampleProduct(PRODUCT_ID_1ST, PRODUCT_STOCK_1ST);
        Member member = createSampleMember();

        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // when, then
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(orderCreateRequest, MEMBER_ID));
    }

    @Test
    @DisplayName("주문 완료 성공")
    public void testCompleteOrder() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Order order = createSampleOrder();
        when(orderRepository.findById(ORDER_ID_1ST)).thenReturn(Optional.of(order));
        when(productRepository.decreaseProductStock(any(Long.class), any(Integer.class))).thenReturn(1);

        // when
        orderService.completeOrder(orderCreateRequest, ORDER_ID_1ST, MEMBER_ID);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER_COMPLETE);
        verify(paymentProductOperation).remove(any(PaymentProduct.class));
        verify(productRepository).decreaseProductStock(any(Long.class), any(Integer.class));
    }

    @Test
    @DisplayName("이미 처리된 주문으로 요청하는 경우 예외 발생")
    public void testCompleteOrder_AlreadyCompleteOrder() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Order order = Order.builder()
                .id(ORDER_ID_1ST)
                .status(OrderStatus.ORDER_COMPLETE)
                .orderDate(LocalDateTime.now())
                .build();
        when(orderRepository.findById(ORDER_ID_1ST)).thenReturn(Optional.of(order));

        // when, then
        assertThrows(OrderNotValidException.class, () -> orderService.completeOrder(orderCreateRequest, ORDER_ID_1ST, MEMBER_ID));
    }

    @Test
    @DisplayName("요청 가능한 기한이 지난 주문으로 요청하는 경우")
    public void testCompleteOrder_OverTimePeriod() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Order order = Order.builder()
                .id(ORDER_ID_1ST)
                .status(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now().minusMinutes(60))
                .build();
        when(orderRepository.findById(ORDER_ID_1ST)).thenReturn(Optional.of(order));

        // when, then
        assertThrows(OrderNotValidException.class, () -> orderService.completeOrder(orderCreateRequest, ORDER_ID_1ST, MEMBER_ID));
    }

    @Test
    @DisplayName("결제 후 재고량이 부족한 경우")
    public void testCompleteOrder_InsufficientStock() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest(QTY_LESS_THAN_STOCK);
        Order order = createSampleOrder();
        when(orderRepository.findById(ORDER_ID_1ST)).thenReturn(Optional.of(order));
        when(productRepository.decreaseProductStock(any(Long.class), any(Integer.class))).thenReturn(0);

        // when, then
        assertThrows(InsufficientStockException.class, () -> orderService.completeOrder(orderCreateRequest, ORDER_ID_1ST, MEMBER_ID));
    }

    private OrderCreateRequest createSampleOrderCreateRequest(int qty) {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(1L, qty);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>();
        orderDetailRequests.add(orderDetailRequest);
        return new OrderCreateRequest(orderDetailRequests, 1000);
    }

    private OrderCreateRequest createMultiProductOrderCreateRequest() {
        OrderDetailRequest orderDetailRequest1 = new OrderDetailRequest(PRODUCT_ID_1ST, PRODUCT_STOCK_1ST);
        OrderDetailRequest orderDetailRequest2 = new OrderDetailRequest(PRODUCT_ID_2ND, PRODUCT_STOCK_2ND);

        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>();
        orderDetailRequests.add(orderDetailRequest1);
        orderDetailRequests.add(orderDetailRequest2);
        return new OrderCreateRequest(orderDetailRequests, 1000);
    }

    private Product createSampleProduct(Long id, int stock) {
        return Product.builder()
                .id(id)
                .price(1000)
                .stock(stock)
                .build();
    }

    private Member createSampleMember() {
        return new Member(MEMBER_ID, "testMember");
    }

    private Order createSampleOrder() {
        return Order.builder()
                .id(ORDER_ID_1ST)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.ORDER)
                .build();
    }

    private OrderDetail createSampleOrderDetail() {
        return  OrderDetail.builder().build();
    }
}
