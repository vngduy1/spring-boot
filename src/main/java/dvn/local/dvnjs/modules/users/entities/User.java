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

    //[lombok]を使用するため、GetterとSetterを使用しない
    // ===== Getter / Setter =====

    // IDを取得
    // public Long getId() {
    //     return id;
    // }

    // // IDを設定
    // public void setId(Long id) {
    //     this.id = id;
    // }

    // // ユーザーカタログIDを取得
    public Long getUserCatalogueId() {
        return userCatalogueId;
    }

    // // ユーザーカタログIDを設定
    public void setUserCatalogueId(Long userCatalogueId) {
        this.userCatalogueId = userCatalogueId;
    }

    // // 名前を取得
    // public String getName() {
    //     return name;
    // }

    // // 名前を設定
    // public void setName(String name) {
    //     this.name = name;
    // }

    // // メールアドレスを取得
    // public String getEmail() {
    //     return email;
    // }

    // // メールアドレスを設定
    // public void setEmail(String email) {
    //     this.email = email;
    // }

    // // パスワードを取得
    // public String getPassword() {
    //     return password;
    // }

    // // パスワードを設定
    // public void setPassword(String password) {
    //     this.password = password;
    // }

    // // 電話番号を取得
    // public String getPhone() {
    //     return phone;
    // }

    // // 電話番号を設定
    // public void setPhone(String phone) {
    //     this.phone = phone;
    // }

    // // 画像パスを取得
    // public String getImage() {
    //     return image;
    // }

    // // 画像パスを設定
    // public void setImage(String image) {
    //     this.image = image;
    // }

    // // アドレスを取得
    // public String getAddress() {
    //     return address;
    // }

    // // アドレスを設定
    // public void setAddress(String address) {
    //     this.address = address;
    // }

    // // 作成日時（読み取り専用）
    // public LocalDateTime getCreatedAt() {
    //     return createAt;
    // }

    // // 更新日時（読み取り専用）
    // public LocalDateTime getUpdatedAt() {
    //     return updateAt;
    // }

    // // 引数付きコンストラクタ（ユーザー情報をまとめて初期化するために使用）
    // // name, email, password, userCatalogueId, phone の各値を受け取り、フィールドに代入する
    // // → new User("Nam", "example@mail.com", "pass", 1L, "0901234567") のように一行で生成できる
    // public User(String name, String email, String password, Long userCatalogueId, String phone) {
    //     this.name = name;
    //     this.email = email;
    //     this.password = password;
    //     this.userCatalogueId = userCatalogueId;
    //     this.phone = phone;
    // }

    // デフォルトコンストラクタ（JPA がエンティティを生成する際に必要）
    // 引数なしでインスタンス化できるようにするため必須
    // public User() {}

}