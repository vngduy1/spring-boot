package dvn.local.dvnjs.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dvn.local.dvnjs.modules.users.entities.User;
import java.util.Optional;

/**
 * User エンティティに対するデータベース操作を行うリポジトリインターフェース。
 * 
 * Spring Data JPA の JpaRepository を継承しており、
 * CRUD 操作（作成、読み取り、更新、削除）が自動的に利用可能になる。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザー情報を検索するメソッド。
     * 
     * Spring Data JPA の命名規則により、
     * メソッド名から自動的に SQL クエリが生成される。
     *
     * @param email 検索対象のメールアドレス
     * @return 該当するユーザーを Optional で返す（存在しない場合は空）
     */
    Optional<User> findByEmail(String email);
}
