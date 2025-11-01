package dvn.local.dvnjs.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;

@Repository
public interface UserCatalogueRepository extends JpaRepository<UserCatalogue, Long> {

    // Optional<UserCatalogues> findById(Long id);
}
