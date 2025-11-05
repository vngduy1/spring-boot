package dvn.local.dvnjs.modules.users.services.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;

public interface UserCatalogueServiceInterface {
    
    UserCatalogue create(StoreRequest request);

    UserCatalogue update(Long id, UpdateRequest request);

    Page<UserCatalogue> paginate(Map<String, String[]> parameters);
    List<UserCatalogue> getAll(Map<String, String[]> parameters);
}
