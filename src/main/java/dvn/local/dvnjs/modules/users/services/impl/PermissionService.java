package dvn.local.dvnjs.modules.users.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.entities.Permission;
import dvn.local.dvnjs.modules.users.mappers.PermissionMapper;
import dvn.local.dvnjs.modules.users.repositories.PermissionRepository;
import dvn.local.dvnjs.modules.users.requests.Permission.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.Permission.UpdateRequest;
import dvn.local.dvnjs.modules.users.services.interfaces.PermissionServiceInterface;
import dvn.local.dvnjs.services.BaseService;

@Service // このクラスは「サービス層」としてSpringコンテナに登録される
public class PermissionService extends BaseService <Permission, PermissionMapper, StoreRequest, UpdateRequest, PermissionRepository> implements PermissionServiceInterface {
    private final PermissionMapper PermissionMapper;
    @Autowired
    private PermissionRepository PermissionRepository; // データベース操作を行うリポジトリ

    // 検索対象フィールドを指定する抽象メソッドの実装
    @Override
    protected String[] getSearchFields() {
        return new String[] { "name" }; // 検索対象フィールドを指定
    }
    // リポジトリを返す抽象メソッドの実装
    @Override
    protected PermissionRepository getRepository() {
        return PermissionRepository; // リポジトリを返す
    }
    @Override
    protected PermissionMapper getMapper() {
        return PermissionMapper; // マッパーを返す
    }
    // コンストラクタインジェクションでサービスを初期化
    public PermissionService(PermissionMapper PermissionMapper) {
        this.PermissionMapper = PermissionMapper;
    }

}
