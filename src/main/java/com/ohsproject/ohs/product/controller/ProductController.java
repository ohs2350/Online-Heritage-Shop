package com.ohsproject.ohs.product.controller;

import com.ohsproject.ohs.product.dto.request.ProductSearchRequest;
import com.ohsproject.ohs.product.dto.response.ProductPageResponse;
import com.ohsproject.ohs.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ProductPageResponse getList(@ModelAttribute @Valid ProductSearchRequest productSearchRequest) {
        return productService.getList(productSearchRequest);
    }
}
