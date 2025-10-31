package dvn.local.dvnjs.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import dvn.local.dvnjs.modules.users.entities.UserCatalogues;


/**
 * User エンティティに対するデータベース操作を行うリポジトリインターフェース。
 * 
 * Spring Data JPA の JpaRepository を継承しており、
 * CRUD 操作（作成、読み取り、更新、削除）が自動的に利用可能になる。
 */
@Repository
public interface UserCatalogueRepository extends JpaRepository<UserCatalogues, Long> {

    // Optional<UserCatalogues> findById(Long id);
}
