package com.ohsproject.ohs.order.exception;

import com.ohsproject.ohs.global.exception.custom.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException() {
        super("해당 주문을 찾을 수 없습니다.");
    }
}
