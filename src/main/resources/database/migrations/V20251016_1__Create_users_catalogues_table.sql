-- ============================================
-- user_cataloguesテーブル作成用SQLスクリプト
-- このスクリプトはユーザーの分類情報（例：管理者、一般ユーザーなど）
-- を管理するためのテーブルを作成します。
-- ============================================

CREATE TABLE user_catalogues (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,   -- カタログID（自動採番・主キー）
    name VARCHAR(50) NOT NULL,                       -- カタログ名（必須）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 登録日時（自動設定）
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP                  -- 更新日時（自動更新）
);
