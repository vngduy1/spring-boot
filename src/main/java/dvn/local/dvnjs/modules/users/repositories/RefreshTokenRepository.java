package dvn.local.dvnjs.modules.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dvn.local.dvnjs.modules.users.entities.RefreshToken;

/**
 * RefreshTokenRepository エンティティに対するデータベース操作を行うリポジトリインターフェース。
 * 
 * Spring Data JPA の JpaRepository を継承しており、
 * 基本的な CRUD 操作（保存、更新、削除、検索）が自動的に利用可能になる。
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
