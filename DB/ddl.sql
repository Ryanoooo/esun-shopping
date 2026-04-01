-- 會員表
CREATE TABLE member (
    member_id   VARCHAR(20)  PRIMARY KEY,
    member_name VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    phone       VARCHAR(20),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 商品表
CREATE TABLE product (
    product_id   VARCHAR(10)   PRIMARY KEY,
    product_name VARCHAR(100)  NOT NULL,
    price        NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    quantity     INTEGER       NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- 訂單表
CREATE TABLE orders (
    order_id    VARCHAR(20)   PRIMARY KEY,
    member_id   VARCHAR(20)   NOT NULL REFERENCES member(member_id),
    total_price NUMERIC(14,2) NOT NULL,
    pay_status  SMALLINT      NOT NULL DEFAULT 0 CHECK (pay_status IN (0,1)),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

-- 訂單明細表
CREATE TABLE order_detail (
    order_item_sn SERIAL        PRIMARY KEY,
    order_id      VARCHAR(20)   NOT NULL REFERENCES orders(order_id),
    product_id    VARCHAR(10)   NOT NULL REFERENCES product(product_id),
    quantity      INTEGER       NOT NULL CHECK (quantity > 0),
    stand_price   NUMERIC(12,2) NOT NULL,
    item_price    NUMERIC(14,2) NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Index
CREATE INDEX idx_orders_member_id        ON orders(member_id);
CREATE INDEX idx_order_detail_order_id   ON order_detail(order_id);
CREATE INDEX idx_order_detail_product_id ON order_detail(product_id);