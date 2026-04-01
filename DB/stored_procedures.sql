-- SP1: 新增商品
CREATE OR REPLACE PROCEDURE sp_insert_product(
    IN p_product_id   VARCHAR,
    IN p_product_name VARCHAR,
    IN p_price        NUMERIC,
    IN p_quantity     INTEGER
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO product(product_id, product_name, price, quantity)
    VALUES (p_product_id, p_product_name, p_price, p_quantity);
END;
$$;


-- SP2: 查詢庫存 > 0 的商品
CREATE OR REPLACE FUNCTION sp_get_products_in_stock()
RETURNS TABLE(
    product_id   VARCHAR,
    product_name VARCHAR,
    price        NUMERIC,
    quantity     INTEGER
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.product_id, p.product_name, p.price, p.quantity
        FROM   product p
        WHERE  p.quantity > 0
        ORDER BY p.product_id;
END;
$$;


-- SP3: 建立訂單（含 Transaction）
CREATE OR REPLACE PROCEDURE sp_create_order(
    IN  p_order_id  VARCHAR,
    IN  p_member_id VARCHAR,
    IN  p_items     JSONB,
    OUT p_success   BOOLEAN,
    OUT p_message   VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_item       JSONB;
    v_product_id VARCHAR;
    v_qty        INTEGER;
    v_stock      INTEGER;
    v_price      NUMERIC;
    v_total      NUMERIC := 0;
BEGIN
    -- 第一輪：驗證庫存
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items) LOOP
        v_product_id := v_item->>'productId';
        v_qty        := (v_item->>'quantity')::INTEGER;

        SELECT quantity, price
        INTO   v_stock, v_price
        FROM   product
        WHERE  product_id = v_product_id
        FOR UPDATE;

        IF v_stock < v_qty THEN
            p_success := FALSE;
            p_message := '商品庫存不足: ' || v_product_id;
            RETURN;
        END IF;

        v_total := v_total + (v_price * v_qty);
    END LOOP;

    -- 寫入訂單
    INSERT INTO orders(order_id, member_id, total_price)
    VALUES (p_order_id, p_member_id, v_total);

    -- 第二輪：寫入明細 + 扣庫存
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items) LOOP
        v_product_id := v_item->>'productId';
        v_qty        := (v_item->>'quantity')::INTEGER;

        SELECT price INTO v_price
        FROM   product
        WHERE  product_id = v_product_id;

        INSERT INTO order_detail(order_id, product_id, quantity, stand_price, item_price)
        VALUES (p_order_id, v_product_id, v_qty, v_price, v_price * v_qty);

        UPDATE product
        SET    quantity = quantity - v_qty
        WHERE  product_id = v_product_id;
    END LOOP;

    p_success := TRUE;
    p_message := '訂單建立成功';

EXCEPTION
    WHEN OTHERS THEN
        p_success := FALSE;
        p_message := '系統錯誤: ' || SQLERRM;
END;
$$;