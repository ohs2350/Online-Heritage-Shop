package com.ohsproject.ohs.product.controller;

import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import com.ohsproject.ohs.product.dto.response.ProductPageResponse;
import com.ohsproject.ohs.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 성공")
    void getList() throws Exception {
        // given
        when(productService.getList(any(ProductSearchRequest.class))).thenReturn(any(ProductPageResponse.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/products?category=1&page=1&listSize=10&sort=priceAsc&minPrice=0&maxPrice=300000")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
        verify(productService).getList(any(ProductSearchRequest.class));
    }

    @Test
    @DisplayName("카테고리 아이디가 포함되어 있지 않으면 요청에 실패한다.")
    void getListWithoutCategory() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/products?&page=1&listSize=10&sort=priceAsc&minPrice=0&maxPrice=300000")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
        verify(productService, never()).getList(any(ProductSearchRequest.class));
    }

    @Test
    @DisplayName("양수가 아닌 값으로 page 번호를 설정하면 요청에 실패한다.")
    void getListWithNotValidPage() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/products?category=1&page=-1&listSize=10&sort=priceAsc&minPrice=0&maxPrice=300000")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
        verify(productService, never()).getList(any(ProductSearchRequest.class));
    }

    @Test
    @DisplayName("음수로 가격 최소 범위를 설정하면 요청에 실패한다.")
    void getListWithNotValidMinPrice() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/products?category=1&page=1&listSize=10&sort=priceAsc&minPrice=-1&maxPrice=300000")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
        verify(productService, never()).getList(any(ProductSearchRequest.class));
    }

    @Test
    @DisplayName("음수로 가격 최대 범위를 설정하면 요청에 실패한다.")
    void getListWithNotValidMaxPrice() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/products?category=1&page=1&listSize=10&sort=priceAsc&minPrice=1&maxPrice=-1")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
        verify(productService, never()).getList(any(ProductSearchRequest.class));
    }
}
