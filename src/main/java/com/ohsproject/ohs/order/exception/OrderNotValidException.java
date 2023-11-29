package com.ohsproject.ohs.order.exception;

import com.ohsproject.ohs.global.exception.custom.BadRequestException;

public class OrderNotValidException extends BadRequestException {

    public OrderNotValidException() {
        super("잘못된 주문에 대한 요청입니다.");
    }
}
