package dvn.local.dvnjs.modules.users.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // エンティティクラス（データベースのテーブルと対応）
@Table(name="refresh_token")
public class RefreshToken {
     // 主キー（自動採番）
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     // トークン文字列
     // nullを許可せず、同じ値を登録できないよう unique=true にする
     @Column(name = "refresh_token")
     private String refreshToken;
 
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

     // ユーザー情報との1対1の関連を定義
     // user_id を外部キーとしてUserテーブルの id と関連付ける
     // このエンティティで user_id を直接変更しないよう insertable=false, updatable=false に設定
     @OneToOne
     @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
     private User user;
}
