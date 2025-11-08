package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;

public interface UserCatalogueServiceInterface extends BaseServiceInterface<UserCatalogue, StoreRequest, UpdateRequest> {
    // 追加のメソッド定義があればここに記述
}
