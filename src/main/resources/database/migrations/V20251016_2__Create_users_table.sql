-- ============================================
-- usersテーブル作成用SQLスクリプト
-- このスクリプトはユーザー情報を管理するためのテーブルを作成します。
-- user_cataloguesテーブルと外部キー制約で紐付けされています。
-- ============================================

CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,        -- ユーザーID（自動採番・主キー）
    user_catalogue_id BIGINT UNSIGNED DEFAULT 0,           -- ユーザーカタログID（外部キー）
    name VARCHAR(50) NOT NULL,                             -- ユーザー名
    email VARCHAR(100) NOT NULL UNIQUE,                    -- メールアドレス（一意制約）
    password VARCHAR(255) NOT NULL,                        -- パスワード（ハッシュ化想定）
    phone VARCHAR(20) NOT NULL UNIQUE,                     -- 電話番号（一意制約）
    address VARCHAR(255) DEFAULT NULL,                     -- 住所（任意）
    image VARCHAR(255) DEFAULT NULL,                       -- プロフィール画像パス（任意）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- 登録日時（自動設定）
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,                       -- 更新日時（自動更新）

    CONSTRAINT fk_user_catalogue_id 
        FOREIGN KEY (user_catalogue_id) 
        REFERENCES user_catalogues(id)
        ON DELETE CASCADE                                  -- 関連データ削除時に自動削除
);
