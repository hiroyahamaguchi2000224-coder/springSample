-- テストユーザー初期データ（パスワードはBCryptハッシュ）
-- user001 / pass001
-- user002 / pass002
-- user003 / pass003
INSERT INTO account (id, user_id, password, user_name, role) VALUES
(1, 'user001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gZvQm2', 'テストユーザー1', 'ADMIN'),
(2, 'user002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gZvQm2', 'テストユーザー2', 'USER'),
(3, 'user003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gZvQm2', 'テストユーザー3', 'USER');

-- メニュー初期データ
INSERT INTO menu (id, screen_id, screen_name, button_name, display_order) VALUES
(1, 'VA0101', 'VA機能', 'VA機能へ', 1),
(2, 'VB0101', 'VB機能', 'VB機能へ', 2);
