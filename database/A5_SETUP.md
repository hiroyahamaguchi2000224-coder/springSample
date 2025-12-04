# A5:SQL Mk-2 での実行手順

## 概要

A5:SQL Mk-2でPostgreSQLデータベースに初期データを投入する手順です。

## 前提条件

1. A5:SQL Mk-2がインストールされている
2. PostgreSQLデータベース（`postgres`）に接続できる
3. スキーマ（テーブル）が作成済み

## 実行手順

### 1. A5:SQL Mk-2を起動してPostgreSQLに接続

1. A5:SQL Mk-2を起動
2. 「データベース」→「データベースの追加と削除」
3. PostgreSQL接続情報を入力：
   - ホスト: `localhost`
   - ポート: `5432`
   - データベース: `postgres`
   - ユーザー: `postgres`
   - パスワード: （設定したパスワード）

### 2. 初期データ投入スクリプトを開く

1. 「ファイル」→「開く」
2. `database/init_data.sql` を開く

### 3. スクリプトを順番に実行

A5:SQL Mk-2では、以下の順番でセクションごとに実行してください：

#### ① pgcrypto拡張機能の有効化

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

実行方法：
1. 該当行を選択
2. `Ctrl + E` または `F5` で実行

#### ② 既存データの削除

```sql
DELETE FROM cart;
DELETE FROM user_company_product_auth;
DELETE FROM product;
DELETE FROM sales_company;
DELETE FROM company;
DELETE FROM users;
DELETE FROM menu;
```

実行方法：
1. 該当セクション全体を選択
2. `Ctrl + E` または `F5` で実行

#### ③ マスタデータの投入

以下のセクションを順番に実行：

1. **会社マスタ** → `INSERT INTO company ...`
2. **販売会社マスタ** → `INSERT INTO sales_company ...`
3. **ユーザーテーブル** → `INSERT INTO users ...` （4件）
4. **商品マスタ** → `INSERT INTO product ...` （5件）
5. **購入可能商品承認** → `INSERT INTO user_company_product_auth ...` （3セクション）
6. **メニューマスタ** → `INSERT INTO menu ...`

各セクションを選択して `Ctrl + E` で実行してください。

#### ④ シーケンスの更新

```sql
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('company_id_seq', (SELECT MAX(id) FROM company));
SELECT setval('sales_company_id_seq', (SELECT MAX(id) FROM sales_company));
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product));
```

実行方法：
1. 該当セクション全体を選択
2. `Ctrl + E` または `F5` で実行

#### ⑤ データ確認

```sql
-- ユーザ一覧
SELECT id, user_id, user_name, role, company_id, enabled, account_locked, created_at FROM users ORDER BY id;

-- 会社一覧
SELECT id, company_code, company_name FROM company ORDER BY id;

-- メニュー一覧
SELECT id, screen_id, screen_name, button_name, display_order, enabled FROM menu ORDER BY display_order;
```

各SELECT文を選択して実行し、データが正しく投入されたか確認してください。

## ログイン情報

| ユーザーID | パスワード | 名前 | ロール | 所属会社 |
|---|---|---|---|---|
| `admin` | `admin123` | 管理者 | ROLE_ADMIN | 株式会社サンプル商事 |
| `user01` | `password123` | 山田太郎 | ROLE_USER | 株式会社サンプル商事 |
| `user02` | `password123` | 佐藤花子 | ROLE_USER | 株式会社テスト物産 |
| `user03` | `password123` | 鈴木一郎 | ROLE_USER | 株式会社デモ貿易 |

## トラブルシューティング

### エラー: 「拡張機能 "pgcrypto" が存在しません」

**原因**: pgcrypto拡張機能が有効化されていない

**解決策**: 
```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```
を先に実行してください。

### エラー: 「外部キー制約違反」

**原因**: データ投入の順序が間違っている

**解決策**: 以下の順番で投入してください：
1. company（親）
2. sales_company（companyに依存）
3. users（companyに依存）
4. product（sales_companyに依存）
5. user_company_product_auth（company、sales_companyに依存）
6. cart（users、productに依存）

### エラー: 「シーケンス "xxx_id_seq" が存在しません」

**原因**: テーブルにデータが投入されていない

**解決策**: INSERTが成功していることを確認してから、シーケンス更新を実行してください。

## Tips

### BCryptハッシュの生成確認

A5:SQL Mk-2で以下のクエリを実行して、BCryptハッシュが正しく生成されているか確認できます：

```sql
-- パスワード検証テスト
SELECT 
    user_id, 
    user_name,
    password = crypt('admin123', password) AS password_match 
FROM users 
WHERE user_id = 'admin';
```

`password_match`が`true`なら正しくハッシュ化されています。

### 新しいユーザーの追加

A5:SQL Mk-2から直接ユーザーを追加する場合：

```sql
INSERT INTO users (user_id, password, user_name, role, company_id, enabled, account_locked) 
VALUES (
    'newuser', 
    crypt('mypassword', gen_salt('bf', 12)),  -- パスワードをその場でハッシュ化
    '新規ユーザー', 
    'ROLE_USER',
    1,  -- 会社ID
    TRUE, 
    FALSE
);
```
