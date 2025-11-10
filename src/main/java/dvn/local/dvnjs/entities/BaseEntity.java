package dvn.local.dvnjs.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@MappedSuperclass
public abstract class BaseEntity {
    @Id // 主キー（Primary Key）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番（オートインクリメント）
    private Long id;

    private String name;  //名前

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

