-- ============================================
-- usersテーブル作成用SQLスクリプト
-- このスクリプトはユーザー情報を管理するためのテーブルを作成します。
-- user_cataloguesテーブルと外部キー制約で紐付けされています。
-- ============================================

CREATE TABLE blacklisted_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,                -- メールアドレス（一意制約）
    user_id BIGINT UNSIGNED NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,                   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- 登録日時（自動設定）
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,                       -- 更新日時（自動更新）

    FOREIGN KEY (user_id) REFERENCES users(id)
);
