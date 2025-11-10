package dvn.local.dvnjs.modules.users.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.entities.Permission;
import dvn.local.dvnjs.modules.users.mappers.PermissionMapper;
import dvn.local.dvnjs.modules.users.repositories.PermissionRepository;
import dvn.local.dvnjs.modules.users.requests.Permission.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.Permission.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.PermissionResource;
import dvn.local.dvnjs.modules.users.services.interfaces.PermissionServiceInterface;

import dvn.local.dvnjs.controllers.BaseController;

// ユーザーカタログに関するREST APIコントローラー
@Validated
@RestController // このクラスがREST APIのコントローラーであることを示す
@RequestMapping("api/v1/auth/permissions") // すべてのエンドポイントの共通URLプレフィックス
public class PermissionController extends BaseController<
    Permission,
    PermissionResource,
    StoreRequest,
    UpdateRequest,
    PermissionRepository
> {

    // コンストラクタインジェクションでサービスを初期化
    public PermissionController(
        PermissionServiceInterface service,
        PermissionMapper mapper,
        PermissionRepository repository
    ) {
        super(service, mapper, repository);  // 親クラスのコンストラクタを呼び出す
    }
    
}
