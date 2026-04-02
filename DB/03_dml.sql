-- 初始管理員帳號（密碼：admin1234）
INSERT INTO member (member_id, member_name, email, password_hash, phone, role) VALUES
    ('ADMIN001', '系統管理員', 'admin@esun.com', crypt('admin1234', gen_salt('bf', 10)), '0900-000-000', 'ADMIN');

-- 測試會員帳號（密碼均為：member1234）
INSERT INTO member (member_id, member_name, email, password_hash, phone, role) VALUES
    ('M20260101001', '王小明', 'member1@esun.com', crypt('member1234', gen_salt('bf', 10)), '0911-111-111', 'MEMBER'),
    ('M20260101002', '李美麗', 'member2@esun.com', crypt('member1234', gen_salt('bf', 10)), '0922-222-222', 'MEMBER'),
    ('M20260101003', '陳大雄', 'member3@esun.com', crypt('member1234', gen_salt('bf', 10)), '0933-333-333', 'MEMBER');

-- 初始商品資料
INSERT INTO product (product_id, product_name, price, quantity) VALUES
    ('P001', 'osii 舒壓按摩椅',  98000, 5),
    ('P002', '網友最愛起司蛋糕',   1200, 50),
    ('P003', '真愛密碼項鍊',       8500, 20);
