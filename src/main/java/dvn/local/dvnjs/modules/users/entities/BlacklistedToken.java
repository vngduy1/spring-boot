package dvn.local.dvnjs.modules.users.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // エンティティクラス（データベースのテーブルと対応）
@Table(name="blacklisted_tokens")
public class BlacklistedToken {
     // 主キー（自動採番）
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     // トークン文字列
     // nullを許可せず、同じ値を登録できないよう unique=true にする
     @Column(nullable = false, unique = true)
     private String token;
 
     // トークンの有効期限
     // 期限を過ぎたトークンは無効とみなす
     @Column(name = "expiry_date", nullable = false)
     private LocalDateTime expiryDate;

     // ユーザーカタログID（他テーブルとの関連用）
    @Column(name="user_id")
    private Long userId;
 
     // レコード作成日時（登録時のみ設定、更新時には変更されない）
     @CreationTimestamp
     @Column(name = "created_at", updatable = false)
     private LocalDateTime createdAt;
 
     // レコード更新日時（更新のたびに変更される）
     @UpdateTimestamp
     @Column(name = "updated_at") 
     private LocalDateTime updatedAt;
}
