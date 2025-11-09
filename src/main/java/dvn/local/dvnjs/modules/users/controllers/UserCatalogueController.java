package dvn.local.dvnjs.modules.users.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.mappers.UserCatalogueMapper;
import dvn.local.dvnjs.modules.users.repositories.UserCatalogueRepository;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;

import dvn.local.dvnjs.controllers.BaseController;

// ユーザーカタログに関するREST APIコントローラー
@Validated
@RestController // このクラスがREST APIのコントローラーであることを示す
@RequestMapping("api/v1/user_catalogues") // すべてのエンドポイントの共通URLプレフィックス
public class UserCatalogueController extends BaseController<
    UserCatalogue,
    UserCatalogueResource,
    StoreRequest,
    UpdateRequest,
    UserCatalogueRepository
> {

    // コンストラクタインジェクションでサービスを初期化
    public UserCatalogueController(
        UserCatalogueServiceInterface service,
        UserCatalogueMapper mapper,
        UserCatalogueRepository repository
    ) {
        super(service, mapper, repository);  // 親クラスのコンストラクタを呼び出す
    }
    
}
