# 玉山銀行 電商購物中心系統

> 玉山銀行後端工程師 Java 實作題

---

## 目錄

- [系統簡介](#系統簡介)
- [功能說明](#功能說明)
- [快速啟動（Docker Compose）](#快速啟動docker-compose)
- [系統架構](#系統架構)
- [技術棧](#技術棧)
- [後端層次架構](#後端層次架構)
- [資料庫設計](#資料庫設計)
- [RESTful API](#restful-api)
- [安全性設計](#安全性設計)
- [本地開發（不使用 Docker Compose）](#本地開發不使用-docker-compose)
- [專案結構](#專案結構)
- [開發者](#開發者)

---

## 系統簡介

實作一個簡易電商購物中心平台，包含會員驗證、商品管理與訂單管理功能，採用三層式架構設計，並透過角色權限（Admin / Member）區分操作範圍。

---

## 功能說明

### 管理員（Admin）
- 登入後進入管理介面
- 新增商品（商品編號、商品名稱、售價、庫存）
- 查看所有訂單（含會員名稱、付款狀態、訂單明細）

### 一般會員（Member）
- 自行註冊帳號
- 瀏覽目前有庫存的商品清單
- 勾選多個商品並設定購買數量（不可超過庫存）
- 即時顯示各商品金額小計及訂單總金額
- 建立訂單後自動扣減商品庫存
- 查看自己的歷史訂單（含付款狀態、訂單明細）

---

## 快速啟動（Docker Compose）

### 環境需求

- Docker & Docker Compose

### 1. 建立環境變數檔

在專案根目錄建立 `.env` 檔：

```env
DB_USER=esun_user
DB_PASS=esun_pass
JWT_SECRET=esun-shopping-secret-key-change-in-production-at-least-32-chars
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

### 2. 啟動所有服務

```bash
docker compose up --build
```

| 服務 | 網址 |
|------|------|
| 前端（Vue.js） | http://localhost:5173 |
| 後端（Spring Boot） | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

### 3. 初始帳號

| 角色 | Email | 密碼 |
|------|-------|------|
| 管理員 | admin@esun.com | admin1234 |
| 測試會員 1 | member1@esun.com | member1234 |
| 測試會員 2 | member2@esun.com | member1234 |
| 測試會員 3 | member3@esun.com | member1234 |

> 一般會員亦可透過系統註冊頁面自行建立帳號。

### 重置資料庫

```bash
docker compose down -v && docker compose up --build
```

---

## 系統架構

```
┌─────────────────┐     RESTful API      ┌──────────────────┐     JDBC      ┌─────────────────┐
│   Vue.js 3      │ ──────────────────►  │  Spring Boot     │ ────────────► │  PostgreSQL 15  │
│   (Port 5173)   │ ◄──────────────────  │  (Port 8080)     │ ◄──────────── │  (Port 5432)    │
│   nginx         │        JSON          │  Spring Security │  Stored Proc  │  Docker         │
└─────────────────┘                      │  JWT Auth        │               └─────────────────┘
     TIER 1                              └──────────────────┘                    TIER 3
   展示層 (Web)                               TIER 2                          資料層 (DB)
                                          應用層 (App)
```

---

## 技術棧

| 類別 | 技術 |
|------|------|
| 前端 | Vue.js 3、Vue Router、Axios、Vite、nginx |
| 後端 | Spring Boot 3.2.5、Java 17、Spring Security |
| 驗證 | JWT（jjwt 0.12.3）、BCrypt 密碼雜湊（pgcrypto） |
| 資料庫 | PostgreSQL 15 |
| 建構工具 | Maven |
| 容器化 | Docker、Docker Compose |
| 安全性 | OWASP Java HTML Sanitizer（XSS防護）、Stored Procedure（SQL Injection防護） |

---

## 後端層次架構

```
com.esun.shopping
├── controller/         # 展示層：接收 HTTP Request，回傳 ResponseEntity
│   ├── AuthController.java
│   ├── MemberController.java
│   ├── ProductController.java
│   └── OrderController.java
├── service/            # 業務層：商業邏輯、@Transactional 管理
│   ├── AuthService.java
│   ├── MemberService.java
│   ├── ProductService.java
│   └── OrderService.java
├── repository/         # 資料層：呼叫 Stored Procedure
│   ├── MemberRepository.java
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── dto/                # 資料傳輸物件
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── MemberDto.java
│   ├── ProductDto.java
│   ├── OrderDto.java
│   ├── OrderItemDto.java
│   └── CreateOrderRequest.java
├── entity/             # 資料實體
│   └── Member.java
└── common/             # 共用層：安全性設定、工具類、例外處理
    ├── ApiResponse.java
    ├── GlobalExceptionHandler.java
    ├── JwtUtil.java
    ├── SecurityConfig.java
    └── filter/
        ├── JwtAuthFilter.java
        ├── XssFilter.java
        └── XssRequestWrapper.java
```

---

## 資料庫設計

### 資料表

| 資料表 | 說明 |
|--------|------|
| `member` | 會員資料（含角色 ADMIN / MEMBER、密碼雜湊） |
| `product` | 商品主檔（編號、名稱、售價、庫存） |
| `orders` | 訂單主檔（編號、會員、總金額、付款狀態） |
| `order_detail` | 訂單明細（商品、數量、單價、小計） |

### Stored Procedures

| SP 名稱 | 類型 | 說明 |
|---------|------|------|
| `sp_register_member` | PROCEDURE | 新增會員 |
| `sp_find_member_by_email` | FUNCTION | 依 Email 查詢會員（含密碼雜湊與角色） |
| `sp_insert_product` | PROCEDURE | 新增商品 |
| `sp_get_products_in_stock` | FUNCTION | 查詢庫存 > 0 的商品 |
| `sp_create_order` | PROCEDURE | 建立訂單（含庫存驗證、Transaction、庫存扣減） |

### DB 腳本位置

```
DB/
├── 01_ddl.sql               # 建立資料表（含 pgcrypto 擴充）
├── 02_stored_procedures.sql # Stored Procedures
└── 03_dml.sql               # 初始資料（管理員帳號）
```

> 腳本依數字前綴順序執行，確保 DDL → SP → DML 的正確初始化順序。

---

## RESTful API

### 驗證 API（無需登入）

| Method | Endpoint | 說明 |
|--------|----------|------|
| POST | `/api/v1/auth/register` | 會員註冊 |
| POST | `/api/v1/auth/login` | 登入（回傳 JWT Token） |

### 商品 API

| Method | Endpoint | 權限 | 說明 |
|--------|----------|------|------|
| GET | `/api/v1/products/in-stock` | 公開 | 查詢有庫存商品 |
| POST | `/api/v1/products` | Admin | 新增商品 |

### 訂單 API

| Method | Endpoint | 權限 | 說明 |
|--------|----------|------|------|
| GET | `/api/v1/orders` | Member | 查詢自己的訂單（含明細） |
| POST | `/api/v1/orders` | Member | 建立訂單 |
| GET | `/api/v1/admin/orders` | Admin | 查詢所有訂單（含會員名稱） |
| PATCH | `/api/v1/admin/orders/{orderId}/pay-status` | Admin | 更新訂單付款狀態 |

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

### JWT 驗證
- 登入後回傳 JWT Token，後續請求於 `Authorization: Bearer <token>` 傳遞
- Token 包含 `memberId`、`role` 資訊，後端透過 `JwtAuthFilter` 解析並注入 Spring Security Context
- Controller 使用 `@AuthenticationPrincipal` 直接取得登入者 ID，無需前端傳遞

### 角色權限控管
- `ADMIN`：可新增商品、查看所有訂單
- `MEMBER`：可建立訂單、查看自己的訂單
- 透過 Spring Security `SecurityFilterChain` 設定 URL 層級的權限規則

### SQL Injection 防護
- 所有資料庫操作透過 **Stored Procedure**，不拼接 SQL 字串
- 使用 `JdbcTemplate` 底層 **Prepared Statement** 參數化查詢
- Controller 層使用 `@Valid` 驗證輸入格式

### XSS 防護
- 自定義 `XssFilter` 攔截所有 HTTP Request
- 使用 **OWASP Java HTML Sanitizer** 清除危險 HTML 標籤

### Transaction 管理
- 建立訂單時同時寫入 `orders`、`order_detail`、扣減 `product` 庫存
- Service 層標記 `@Transactional`，任一步驟失敗自動 Rollback
- SP 內使用 `FOR UPDATE` 鎖定商品列，防止超賣（Race Condition）

---

## 本地開發（不使用 Docker Compose）

### 環境需求

- Java 17+、Maven 3.8+、Node.js 20+、PostgreSQL 15

### 1. 建立資料庫

```bash
psql -U postgres -c "CREATE DATABASE esun_shopping WITH ENCODING 'UTF8';"
psql -U postgres -d esun_shopping -f DB/01_ddl.sql
psql -U postgres -d esun_shopping -f DB/02_stored_procedures.sql
psql -U postgres -d esun_shopping -f DB/03_dml.sql
```

### 2. 啟動後端

```bash
cd backend
mvn spring-boot:run
```

後端服務啟動於：`http://localhost:8080`

### 3. 啟動前端

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
├── .env                          # 環境變數（需自行建立，不納入版控）
├── docker-compose.yml
├── DB/
│   ├── 01_ddl.sql
│   ├── 02_stored_procedures.sql
│   └── 03_dml.sql
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/esun/shopping/
│       ├── EsunShoppingApplication.java
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── dto/
│       ├── entity/
│       └── common/
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
        ├── App.vue
        ├── main.js
        ├── store/
        ├── router/
        ├── api/
        └── views/
            ├── Login.vue
            ├── admin/
            │   ├── ProductManage.vue
            │   └── AdminOrders.vue
            └── member/
                ├── MyOrders.vue
                └── CreateOrder.vue
```

---

## 開發者

- GitHub：[@Ryanoooo](https://github.com/Ryanoooo)
