package dvn.local.dvnjs.modules.users.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dvn.local.dvnjs.modules.users.entities.RefreshToken;


/**
 * RefreshTokenRepository エンティティに対するデータベース操作を行うリポジトリインターフェース。
 * 
 * Spring Data JPA の JpaRepository を継承しており、
 * 基本的な CRUD 操作（保存、更新、削除、検索）が自動的に利用可能になる。
 * 
 * 追加メソッドとして、リフレッシュトークン文字列やユーザーIDによる検索を提供する。
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 指定されたリフレッシュトークン文字列が既に存在するかどうかを判定する。
     * 
     * @param refreshToken チェック対象のトークン文字列
     * @return トークンが存在すれば true、存在しなければ false
     */
    boolean existsByRefreshToken(String refreshToken);

    /**
     * リフレッシュトークン文字列からトークン情報を取得する。
     * 
     * @param refreshToken 検索対象のトークン文字列
     * @return 一致するトークンが存在する場合は Optional にラップして返す
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    /**
     * ユーザーIDからリフレッシュトークン情報を取得する。
     * 
     * @param userId 検索対象のユーザーID
     * @return 一致するトークンが存在する場合は Optional にラップして返す
     */
    Optional<RefreshToken> findByUserId(Long userId);

    int deleteByExpiryDateBefore(LocalDateTime currentDateTime);
}
