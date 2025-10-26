-- ============================================
-- refresh_tokens テーブル作成用 SQL スクリプト
-- このスクリプトはユーザーのリフレッシュトークンを管理するためのテーブルを作成します。
-- users テーブルと外部キー制約で紐付けられます。
-- ============================================

CREATE TABLE refresh_token (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,         -- 主キー（自動採番）
    user_id BIGINT UNSIGNED NOT NULL,                      -- ユーザーID（usersテーブルと関連付け）
    refresh_token TEXT NOT NULL UNIQUE,                    -- リフレッシュトークン（重複不可）
    expiry_date TIMESTAMP NOT NULL,                        -- トークンの有効期限
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- 登録日時（自動設定）
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,                       -- 更新日時（更新時に自動更新）

    FOREIGN KEY (user_id) REFERENCES users(id)              -- 外部キー制約：usersテーブルのidを参照
);
