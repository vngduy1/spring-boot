package dvn.local.dvnjs.modules.users.services.impl;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.mappers.UserCatalogueMapper;
import dvn.local.dvnjs.modules.users.repositories.UserCatalogueRepository;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import dvn.local.dvnjs.services.BaseService;
@Service // このクラスは「サービス層」としてSpringコンテナに登録される
public class UserCatalogueService extends BaseService <UserCatalogue, UserCatalogueMapper, StoreRequest, UpdateRequest, UserCatalogueRepository> implements UserCatalogueServiceInterface {

    private final UserCatalogueMapper userCatalogueMapper;

    @Autowired
    private UserCatalogueRepository userCatalogueRepository; // データベース操作を行うリポジトリ

    // private static final Logger logger = LoggerFactory.getLogger(UserCatalogueService.class); // ロガーの設定

    // 検索対象フィールドを指定する抽象メソッドの実装
    @Override
    protected String[] getSearchFields() {
        return new String[] { "name" }; // 検索対象フィールドを指定
    }

    // リポジトリを返す抽象メソッドの実装
    @Override
    protected UserCatalogueRepository getRepository() {
        return userCatalogueRepository; // リポジトリを返す
    }

    @Override
    protected String[] getRelations() {
        return new String[] { "permissions" }; // 関連エンティティを指定
    }

    // コンストラクタインジェクションでサービスを初期化
    public UserCatalogueService(UserCatalogueMapper userCatalogueMapper) {
        this.userCatalogueMapper = userCatalogueMapper;
    }

    // マッパーを返す抽象メソッドの実装
    @Override
    protected UserCatalogueMapper getMapper() {
        return userCatalogueMapper; // マッパーを返す
    }

}
