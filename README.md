# 玉山銀行 電商購物中心系統

> [新進人員] 玉山銀行後端工程師 Java 實作題

---

## 系統簡介

實作一個簡易電商購物中心平台，包含商品管理與訂單管理功能，採用三層式架構設計。

---

## 功能說明

### 商品管理
- 新增商品（商品編號、商品名稱、售價、庫存）
- 顯示目前有庫存的商品清單

### 訂單管理
- 顯示庫存大於零的商品清單
- 顧客可勾選多個商品並設定購買數量
- 購買數量不能超過該商品庫存量
- 即時顯示各商品金額小計及訂單總金額
- 建立訂單後自動扣減商品庫存

---

## 系統架構

```
┌─────────────────┐     RESTful API      ┌──────────────────┐     JDBC      ┌─────────────────┐
│   Vue.js 3      │ ──────────────────►  │  Spring Boot     │ ────────────► │  PostgreSQL 16  │
│   (Port 5173)   │ ◄──────────────────  │  (Port 8080)     │ ◄──────────── │  (Port 5432)    │
│   Vite          │        JSON          │  Tomcat          │  Stored Proc  │  Docker         │
└─────────────────┘                      └──────────────────┘               └─────────────────┘
     TIER 1                                    TIER 2                             TIER 3
   展示層 (Web)                            應用層 (App)                         資料層 (DB)
```

---

## 技術棧

| 類別 | 技術 |
|------|------|
| 前端 | Vue.js 3、Vue Router、Axios、Vite |
| 後端 | Spring Boot 3.2.5、Java 17 |
| 資料庫 | PostgreSQL 16 |
| 建構工具 | Maven |
| 容器化 | Docker |
| 安全性 | OWASP Java HTML Sanitizer（XSS防護）、Stored Procedure（SQL Injection防護） |

---

## 後端層次架構

```
com.esun.shopping
├── controller/         # 展示層：接收 HTTP Request，回傳 ResponseEntity
│   ├── ProductController.java
│   └── OrderController.java
├── service/            # 業務層：商業邏輯、@Transactional 管理
│   ├── ProductService.java
│   └── OrderService.java
├── repository/         # 資料層：呼叫 Stored Procedure
│   ├── ProductRepository.java
│   └── OrderRepository.java
└── common/             # 共用層：DTO、例外處理、安全性
    ├── ApiResponse.java
    ├── GlobalExceptionHandler.java
    ├── CorsConfig.java
    ├── dto/
    │   ├── ProductDto.java
    │   ├── OrderDto.java
    │   ├── OrderItemDto.java
    │   └── CreateOrderRequest.java
    └── filter/
        ├── XssFilter.java
        └── XssRequestWrapper.java
```

---

## 資料庫設計

### 資料表

| 資料表 | 說明 |
|--------|------|
| `member` | 會員資料 |
| `product` | 商品主檔（編號、名稱、售價、庫存） |
| `orders` | 訂單主檔（編號、會員、總金額、付款狀態） |
| `order_detail` | 訂單明細（商品、數量、單價、小計） |

### Stored Procedures

| SP 名稱 | 說明 |
|---------|------|
| `sp_insert_product` | 新增商品 |
| `sp_get_products_in_stock` | 查詢庫存 > 0 的商品 |
| `sp_create_order` | 建立訂單（含庫存驗證、Transaction、庫存扣減） |

### DB 腳本位置
```
DB/
├── ddl.sql                  # 建立資料表
├── dml.sql                  # 初始測試資料
└── stored_procedures.sql    # Stored Procedures
```

---

## RESTful API

### 商品 API

| Method | Endpoint | 說明 |
|--------|----------|------|
| GET | `/api/v1/products/in-stock` | 查詢有庫存商品 |
| POST | `/api/v1/products` | 新增商品 |

### 訂單 API

| Method | Endpoint | 說明 |
|--------|----------|------|
| GET | `/api/v1/orders` | 查詢所有訂單 |
| POST | `/api/v1/orders` | 建立訂單 |

### 統一回應格式

```json
{
  "success": true,
  "code": "200",
  "message": "查詢成功",
  "data": { },
  "timestamp": "2026-04-01 13:00:00"
}
```

---

## 安全性設計

### SQL Injection 防護
- 所有資料庫操作透過 **Stored Procedure**，不拼接 SQL 字串
- 使用 `JdbcTemplate` 底層 **Prepared Statement** 參數化查詢
- Controller 層使用 `@Valid` 驗證輸入格式

### XSS 防護
- 自定義 `XssFilter` 攔截所有 HTTP Request
- 使用 **OWASP Java HTML Sanitizer** 清除危險 HTML 標籤
- 前端使用 `v-text` 顯示用戶輸入，避免 DOM-based XSS

### Transaction 管理
- 建立訂單時同時寫入 `orders`、`order_detail`、扣減 `product` 庫存
- Service 層標記 `@Transactional`，任一步驟失敗自動 Rollback
- SP 內使用 `FOR UPDATE` 鎖定商品列，防止超賣（Race Condition）

---

## 環境需求

- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker
- PostgreSQL 16（透過 Docker 運行）

---

## 快速啟動

### 1. 啟動 PostgreSQL

```bash
docker run --name esun-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -p 5432:5432 \
  -d postgres:16
```

### 2. 建立資料庫與資料表

```bash
docker exec -it esun-postgres psql -U postgres -c "CREATE DATABASE esun_shopping WITH ENCODING 'UTF8';"

docker exec -i esun-postgres psql -U postgres -d esun_shopping < DB/ddl.sql
docker exec -i esun-postgres psql -U postgres -d esun_shopping < DB/stored_procedures.sql
docker exec -i esun-postgres psql -U postgres -d esun_shopping < DB/dml.sql
```

### 3. 啟動後端

```bash
cd backend/esun-shopping-backend
mvn spring-boot:run
```

後端服務啟動於：`http://localhost:8080`

### 4. 啟動前端

```bash
cd frontend
npm install
npm run dev
```

前端服務啟動於：`http://localhost:5173`

---

## 專案結構

```
esun-shopping/
├── DB/
│   ├── ddl.sql
│   ├── dml.sql
│   └── stored_procedures.sql
├── backend/
│   └── esun-shopping-backend/
│       ├── pom.xml
│       └── src/main/java/com/esun/shopping/
│           ├── EsunShoppingApplication.java
│           ├── controller/
│           ├── service/
│           ├── repository/
│           └── common/
└── frontend/
    ├── package.json
    └── src/
        ├── App.vue
        ├── main.js
        ├── router/
        ├── api/
        └── views/
```

---

## 開發者

- GitHub：[@Ryanoooo](https://github.com/Ryanoooo)