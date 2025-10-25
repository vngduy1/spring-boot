package dvn.local.dvnjs.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dvn.local.dvnjs.modules.users.entities.BlacklistedToken;

/**
 * BlacklistedToken エンティティに対するデータベース操作を行うリポジトリインターフェース。
 * 
 * Spring Data JPA の JpaRepository を継承しており、
 * 基本的な CRUD 操作（保存、更新、削除、検索）が自動的に利用可能になる。
 */
@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * 指定されたトークンがデータベースに存在するかを確認するメソッド。
     * 
     * @param token チェック対象のJWTトークン文字列
     * @return 存在する場合は true、存在しない場合は false
     */
    boolean existsByToken(String token);
}
