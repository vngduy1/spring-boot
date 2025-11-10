-- ============================================
-- user_cataloguesテーブル作成用SQLスクリプト
-- このスクリプトはユーザーの分類情報（例：管理者、一般ユーザーなど）
-- を管理するためのテーブルを作成します。
-- ============================================

CREATE TABLE user_catalogues_permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,   -- カタログID（自動採番・主キー）
    user_catalogues_id BIGINT UNSIGNED NOT NULL,  -- user_cataloguesテーブルの外部キー
    permissions_id BIGINT UNSIGNED NOT NULL,      -- permissionsテーブルの外部キー
    CONSTRAINT fk_user_catalogues
        FOREIGN KEY (user_catalogues_id)
        REFERENCES user_catalogues(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_permissions
        FOREIGN KEY (permissions_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
