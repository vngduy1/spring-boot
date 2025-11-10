package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.entities.Permission;
import dvn.local.dvnjs.modules.users.requests.Permission.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.Permission.UpdateRequest;

public interface PermissionServiceInterface extends BaseServiceInterface<Permission, StoreRequest, UpdateRequest> {
    // 追加のメソッド定義があればここに記述
}
