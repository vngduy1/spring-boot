package dvn.local.dvnjs.modules.users.entities;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder(toBuilder=true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // エンティティクラス（データベースのテーブルと対応）
@Table(name = "user_catalogues")
public class UserCatalogue {
    @Id // 主キー（Primary Key）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番（オートインクリメント）
    private Long id;

    private String name; //名前

    // 権限との多対多の関係を定義
    @ManyToMany
    @JoinTable(
        name = "user_catalogue_permissions",
        joinColumns = @JoinColumn(name = "user_catalogue_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions; // 権限の集合

    @Column(name = "publish", nullable = false, columnDefinition = "TINYINT")
    private Integer publish;

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
}
