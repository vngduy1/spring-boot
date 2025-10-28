package dvn.local.dvnjs.modules.users.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // エンティティクラス（データベースのテーブルと対応）
@Table(name="users")
public class User {
    
    @Id // 主キー（Primary Key）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番（オートインクリメント）
    private Long id;

    // ユーザーカタログID（他テーブルとの関連用）
    @Column(name="user_catalogue_id")
    private Long userCatalogueId;

    // ユーザー名
    private String name;

    // メールアドレス
    private String email;

    // パスワード
    private String password;

    // 電話番号
    private String phone;

    // プロフィール画像パス
    private String image;

    // アドレス
    private String address;

    // 作成日時（新規登録時のみ設定）
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    // 更新日時（更新時のみ設定）
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    // レコード作成前に呼び出される（作成日時を自動設定）
    @PrePersist
    protected void onCreated() {
        createdAt = LocalDateTime.now();
    }

    // レコード更新前に呼び出される（更新日時を自動設定）
    @PreUpdate
    protected void onUpdated() {
        updatedAt = LocalDateTime.now();
    }

    // // ユーザーカタログIDを取得
    public Long getUserCatalogueId() {
        return userCatalogueId;
    }

    // // ユーザーカタログIDを設定
    public void setUserCatalogueId(Long userCatalogueId) {
        this.userCatalogueId = userCatalogueId;
    }

}