-- ============================================
-- user_catalogues_permissionsテーブル作成用SQLスクリプト
-- このスクリプトはユーザーの分類情報（例：管理者、一般ユーザーなど）
-- を管理するためのテーブルを作成します。
-- ============================================

CREATE TABLE user_catalogue_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,   -- カタログID（自動採番・主キー）
    user_catalogue_id BIGINT UNSIGNED NOT NULL,  -- user_cataloguesテーブルの外部キー
    user_id BIGINT UNSIGNED NOT NULL,      -- permissionsテーブルの外部キー
    CONSTRAINT fk_user_catalogue_user_id
        FOREIGN KEY (user_catalogue_id)
        REFERENCES user_catalogues(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
