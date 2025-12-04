-- ============================================================
-- 初期データ投入スクリプト（PostgreSQL用）
-- ============================================================
-- スキーマ: SAMPLE
-- 実行方法：
-- A5:SQL Mk-2 から実行する場合は、セクションごとに分けて実行してください
-- コマンドラインの場合： psql -U postgres -d postgres -f database/init_data.sql
-- ============================================================

-- ------------------------------------------------------------
-- pgcrypto拡張機能を有効化（初回のみ）
-- ------------------------------------------------------------
-- A5:SQL Mk-2: このセクションを先に実行してください
CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- ------------------------------------------------------------
-- 既存データの削除（クリーンインストール用）
-- ------------------------------------------------------------
-- 外部キー制約がないため、順序を気にせず削除可能
TRUNCATE TABLE SAMPLE.TT_CART;
TRUNCATE TABLE SAMPLE.TM_COMPANY_PRODUCT_AUTH;
TRUNCATE TABLE SAMPLE.TM_PRODUCT;
TRUNCATE TABLE SAMPLE.TM_USER_COMPANY;
TRUNCATE TABLE SAMPLE.TM_COMPANY;
TRUNCATE TABLE SAMPLE.TM_USER;


-- ------------------------------------------------------------
-- 会社マスタの初期データ
-- ------------------------------------------------------------
INSERT INTO SAMPLE.TM_COMPANY (COMPANY_ID, COMPANY_NAME, DEL_FLG) VALUES
('C001', '株式会社サンプル商事', FALSE),
('C002', '株式会社テスト物産', FALSE),
('C003', '株式会社デモ貿易', FALSE),
('SC001', 'サンプル販売株式会社', FALSE),
('SC002', 'サンプル流通株式会社', FALSE),
('SC003', 'テスト販売株式会社', FALSE),
('SC004', 'デモ販売株式会社', FALSE);


-- ------------------------------------------------------------
-- ユーザマスタの初期データ
-- ------------------------------------------------------------
-- 注意: パスワードはBCryptでDB側でハッシュ化して保存

-- 管理者アカウント（user_id: admin, password: admin123）
INSERT INTO SAMPLE.TM_USER (USER_ID, PASSWORD, USER_NAME, ROLE, ACCOUNT_LOCKED, DEL_FLG) VALUES
('admin', crypt('admin123', gen_salt('bf', 12)), '管理者', 'ROLE_ADMIN', FALSE, FALSE);

-- 一般ユーザーアカウント（user_id: user01, password: password123）
INSERT INTO SAMPLE.TM_USER (USER_ID, PASSWORD, USER_NAME, ROLE, ACCOUNT_LOCKED, DEL_FLG) VALUES
('user01', crypt('password123', gen_salt('bf', 12)), '山田太郎', 'ROLE_USER', FALSE, FALSE);

-- 一般ユーザーアカウント（user_id: user02, password: password123）
INSERT INTO SAMPLE.TM_USER (USER_ID, PASSWORD, USER_NAME, ROLE, ACCOUNT_LOCKED, DEL_FLG) VALUES
('user02', crypt('password123', gen_salt('bf', 12)), '佐藤花子', 'ROLE_USER', FALSE, FALSE);

-- 一般ユーザーアカウント（user_id: user03, password: password123）
INSERT INTO SAMPLE.TM_USER (USER_ID, PASSWORD, USER_NAME, ROLE, ACCOUNT_LOCKED, DEL_FLG) VALUES
('user03', crypt('password123', gen_salt('bf', 12)), '鈴木一郎', 'ROLE_USER', FALSE, FALSE);


-- ------------------------------------------------------------
-- ユーザ所属マスタの初期データ
-- ------------------------------------------------------------
INSERT INTO SAMPLE.TM_USER_COMPANY (USER_ID, COMPANY_ID, DEL_FLG) VALUES
('admin', 'C001', FALSE),
('user01', 'C001', FALSE),
('user02', 'C002', FALSE),
('user03', 'C003', FALSE);


-- ------------------------------------------------------------
-- 商品マスタの初期データ
-- ------------------------------------------------------------
INSERT INTO SAMPLE.TM_PRODUCT (PRODUCT_ID, PRODUCT_NAME, COMPANY_ID, PRICE, STOCK_QUANTITY, DESCRIPTION, DEL_FLG) VALUES
('P001', 'ノートパソコン Lenovo ThinkPad', 'SC001', 120000.00, 50, '高性能ビジネスノートPC', FALSE),
('P002', 'デスクトップPC Dell OptiPlex', 'SC001', 80000.00, 30, 'オフィス向けデスクトップ', FALSE),
('P003', 'モニター 27インチ 4K', 'SC002', 45000.00, 100, '4K解像度の大型モニター', FALSE),
('P004', 'キーボード 日本語配列', 'SC002', 3500.00, 200, '静音設計のキーボード', FALSE),
('P005', 'マウス ワイヤレス', 'SC002', 2500.00, 300, 'エルゴノミクスデザイン', FALSE);


-- ------------------------------------------------------------
-- 会社別購入可能商品承認マスタの初期データ
-- ------------------------------------------------------------
-- 株式会社サンプル商事(C001)は、SC001、SC002、SC003から購入可能
INSERT INTO SAMPLE.TM_COMPANY_PRODUCT_AUTH (COMPANY_ID, SALES_COMPANY_ID, DEL_FLG) VALUES
('C001', 'SC001', FALSE),  -- サンプル商事 → サンプル販売
('C001', 'SC002', FALSE),  -- サンプル商事 → サンプル流通
('C001', 'SC003', FALSE);  -- サンプル商事 → テスト販売

-- 株式会社テスト物産(C002)は、SC002、SC003から購入可能
INSERT INTO SAMPLE.TM_COMPANY_PRODUCT_AUTH (COMPANY_ID, SALES_COMPANY_ID, DEL_FLG) VALUES
('C002', 'SC002', FALSE),  -- テスト物産 → サンプル流通
('C002', 'SC003', FALSE);  -- テスト物産 → テスト販売

-- 株式会社デモ貿易(C003)は、SC004のみ購入可能
INSERT INTO SAMPLE.TM_COMPANY_PRODUCT_AUTH (COMPANY_ID, SALES_COMPANY_ID, DEL_FLG) VALUES
('C003', 'SC004', FALSE);  -- デモ貿易 → デモ販売

-- ------------------------------------------------------------
-- データ確認用クエリ
-- ------------------------------------------------------------
-- ユーザ一覧
SELECT USER_ID, USER_NAME, ROLE, ACCOUNT_LOCKED, DEL_FLG, CREATED_AT FROM SAMPLE.TM_USER ORDER BY USER_ID;

-- ユーザ所属一覧
SELECT UC.USER_ID, U.USER_NAME, UC.COMPANY_ID, C.COMPANY_NAME 
FROM SAMPLE.TM_USER_COMPANY UC
JOIN SAMPLE.TM_USER U ON UC.USER_ID = U.USER_ID
JOIN SAMPLE.TM_COMPANY C ON UC.COMPANY_ID = C.COMPANY_ID
ORDER BY UC.USER_ID;

-- 会社一覧
SELECT COMPANY_ID, COMPANY_NAME, DEL_FLG FROM SAMPLE.TM_COMPANY ORDER BY COMPANY_ID;

-- 商品一覧
SELECT PRODUCT_ID, PRODUCT_NAME, COMPANY_ID, PRICE, STOCK_QUANTITY FROM SAMPLE.TM_PRODUCT ORDER BY PRODUCT_ID;

-- パスワード検証テスト（admin123でログインできるか確認）
-- SELECT USER_ID, PASSWORD = crypt('admin123', PASSWORD) AS password_match FROM SAMPLE.TM_USER WHERE USER_ID = 'admin';
