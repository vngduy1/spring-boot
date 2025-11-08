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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
    protected UserCatalogueMapper getMapper() {
        return userCatalogueMapper; // マッパーを返す
    }

    // コンストラクタインジェクションでサービスを初期化
    public UserCatalogueService(UserCatalogueMapper userCatalogueMapper) {
        this.userCatalogueMapper = userCatalogueMapper;
    }

    /**
     * 更新処理
     * - 指定IDのデータを取得し、存在しない場合はEntityNotFoundExceptionをスロー
     * - 更新後のデータを保存
     */
    // @Override
    // @Transactional
    // public UserCatalogue update(Long id, UpdateRequest request) {
    //     // 対象データを取得。存在しなければ例外をスロー
    //     UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException("対象データが存在しません"));
    //     // リクエストデータでエンティティを更新
    //     userCatalogueMapper.updateEntityFromResource(request, userCatalogue);
    //     // 更新データを保存
    //     return userCatalogueRepository.save(userCatalogue);
    // }

    // @Override
    // @Transactional
    // public Boolean delete(Long id) {
    //     UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
    //                 .orElseThrow(() -> new EntityNotFoundException("対象データが存在しません"));
    //         userCatalogueRepository.delete(userCatalogue); // データを削除
    //         return true; // 削除成功
    // }

    // @Override
    // @Transactional
    // public Boolean deleteMultipleEntity(List<Long> ids) {
    //     List<UserCatalogue> userCatalogues = userCatalogueRepository.findAllById(ids);
    //     if(userCatalogues.size() != ids.size()) {
    //         throw new EntityNotFoundException("削除対象のデータが存在しません");
    //     }
    //     userCatalogueRepository.deleteAll(userCatalogues);
    //     return true; // 削除成功
    // }
}
