package dvn.local.dvnjs.modules.users.services.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;

public interface UserCatalogueServiceInterface {
    // CRUD操作のインターフェース定義
    UserCatalogue create(StoreRequest request);

    // updateメソッドのシグネチャ
    UserCatalogue update(Long id, UpdateRequest request);

    // deleteメソッドのシグネチャ
    Boolean delete(Long id);

    Boolean deleteMultipleEntity(List<Long> id);
    
    Page<UserCatalogue> paginate(Map<String, String[]> parameters);
    List<UserCatalogue> getAll(Map<String, String[]> parameters);
}
