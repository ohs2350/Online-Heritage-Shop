CREATE TABLE member (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(50)
);

CREATE TABLE product (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    price NUMBER(10, 2),
    stock NUMBER,
    hit NUMBER
);

CREATE TABLE orders (
    id NUMBER PRIMARY KEY,
    member_id NUMBER,
    order_date DATE,
    amount NUMBER(10, 2),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE order_item (
    id NUMBER PRIMARY KEY,
    order_id NUMBER,
    product_id NUMBER,
    qty NUMBER,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);