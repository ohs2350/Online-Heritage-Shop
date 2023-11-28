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
import com.ohsproject.ohs.order.exception.OrderNotValidException;
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

import static com.ohsproject.ohs.support.fixture.MemberFixture.createMember;
import static com.ohsproject.ohs.support.fixture.OrderFixture.createOrder;
import static com.ohsproject.ohs.support.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("단일 상품 주문에 성공하는 경우")
    void placeOrderWithSingleProduct() {
        // given
        Product product = createProduct();
        Member member = createMember();
        Order order = createOrder();
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                List.of(new OrderDetailRequest(product.getId(), product.getStock())),
                1000);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentProductOperation.count(anyLong())).thenReturn(0L);

        // when
        Long orderId = orderService.placeOrder(orderCreateRequest, member.getId());

        // then
        assertAll(
                () -> assertThat(orderId).isNotNull(),
                () -> assertEquals(orderId, order.getId()),
                () -> verify(memberRepository, times(1)).findById(member.getId()),
                () -> verify(orderRepository, times(1)).save(any(Order.class)),
                () -> verify(orderDetailRepository, times(1)).save(any(OrderDetail.class))
        );
    }

    @Test
    @DisplayName("다중 상품 주문에 성공하는 경우")
    void placeOrderWithMultipleProduct() {
        // given
        Product product1 = createProduct(1L, 10);
        Product product2 = createProduct(2L, 20);
        Member member = createMember();
        Order order = createOrder();
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                List.of(
                        new OrderDetailRequest(product1.getId(), product1.getStock()),
                        new OrderDetailRequest(product2.getId(), product2.getStock())),
                1000);
        int count = orderCreateRequest.getOrderDetailRequests().size();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        when(productRepository.findById(product2.getId())).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentProductOperation.count(anyLong())).thenReturn(0L);

        // when
        Long orderId = orderService.placeOrder(orderCreateRequest, member.getId());

        // then
        assertAll(
                () -> assertThat(orderId).isNotNull(),
                () -> assertEquals(orderId, order.getId()),
                () -> verify(memberRepository, times(1)).findById(member.getId()),
                () -> verify(orderRepository, times(1)).save(any(Order.class)),
                () -> verify(productRepository, times(2)).findById(product1.getId()),
                () -> verify(productRepository, times(2)).findById(product2.getId()),
                () -> verify(orderDetailRepository, times(count)).save(any(OrderDetail.class))
        );
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 주문하는 경우 예외가 발생한다.")
    public void placeOrderWithNotValidMemberId() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> orderService.placeOrder(orderCreateRequest, memberId));
    }

    @Test
    @DisplayName("잘못된 상품번호로 주문하는 경우 예외가 발생한다.")
    public void placeOrderWithNotValidProductId() {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        Member member = createMember();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> orderService.placeOrder(orderCreateRequest, member.getId()));
    }

    @Test
    @DisplayName("주문한 상품의 재고량이 부족한 경우 예외가 발생한다.")
    public void placeOrderWithInsufficientStock() {
        // given
        Product product = createProduct(1L, 5);
        Member member = createMember();
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                List.of(new OrderDetailRequest(1L, 6)),
                1000);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // when, then
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(orderCreateRequest, member.getId()));
    }

    @Test
    @DisplayName("주문 완료 성공")
    public void completeOrder() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Order order = createOrder();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(productRepository.decreaseProductStock(any(Long.class), any(Integer.class))).thenReturn(1);

        // when
        orderService.completeOrder(orderCompleteRequest, order.getId(), 1L);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER_COMPLETE);
        verify(paymentProductOperation).remove(any(PaymentProduct.class));
        verify(productRepository).decreaseProductStock(any(Long.class), any(Integer.class));
    }

    @Test
    @DisplayName("존재하지 않는 주문번호로 요청하는 경우 예외가 발생한다,")
    public void completeOrderWithNotValidOrder() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(OrderNotFoundException.class, () -> orderService.completeOrder(orderCompleteRequest, orderId, 1L));
    }

    @Test
    @DisplayName("이미 처리된 주문으로 요청하는 경우 예외가 발생한다,")
    public void completeOrderWithCompletedOrder() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Order order = createOrder(OrderStatus.ORDER_COMPLETE);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when, then
        assertThrows(OrderNotValidException.class, () -> orderService.completeOrder(orderCompleteRequest, order.getId(), 1L));
    }

    @Test
    @DisplayName("취소된 주문으로 요청하는 경우 예외가 발생한다,")
    public void completeOrderWithCanceledOrder() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Order order = createOrder(OrderStatus.CANCEL);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when, then
        assertThrows(OrderNotValidException.class, () -> orderService.completeOrder(orderCompleteRequest, order.getId(), 1L));
    }

    @Test
    @DisplayName("유효기간이 지난 주문으로 요청하는 경우 예외가 발생한다.")
    public void completeOrderWithOverPeriodOrder() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now().minusMinutes(60))
                .build();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when, then
        assertThrows(OrderNotValidException.class, () -> orderService.completeOrder(orderCompleteRequest, order.getId(),1L));
    }

    @Test
    @DisplayName("결제 후 재고량이 부족한 경우 예외가 발생한다.")
    public void completeOrderWithInsufficientStock() {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        Order order = createOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.decreaseProductStock(any(Long.class), any(Integer.class))).thenReturn(0);

        // when, then
        assertThrows(InsufficientStockException.class, () -> orderService.completeOrder(orderCompleteRequest, order.getId(), 1L));
    }

    private OrderCreateRequest createSampleOrderCreateRequest() {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(PRODUCT_ID, PRODUCT_STOCK);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>(List.of(orderDetailRequest));
        return new OrderCreateRequest(orderDetailRequests, 1000);
    }

    private OrderCompleteRequest createSampleOrderCompleteRequest() {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(PRODUCT_ID, PRODUCT_STOCK);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>(List.of(orderDetailRequest));
        return new OrderCompleteRequest(orderDetailRequests, 1000);
    }

}
