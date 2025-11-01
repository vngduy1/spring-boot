package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;

public interface UserCatalogueServiceInterface {
    
    UserCatalogue create(StoreRequest request);
    UserCatalogue update(Long id,UpdateRequest request);
}
