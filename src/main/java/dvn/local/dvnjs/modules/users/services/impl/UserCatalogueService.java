package dvn.local.dvnjs.modules.users.services.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.helpers.FilterParameter;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.repositories.UserCatalogueRepository;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import dvn.local.dvnjs.services.BaseService;
import dvn.local.dvnjs.specifications.BaseSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service // このクラスは「サービス層」としてSpringコンテナに登録される
public class UserCatalogueService extends BaseService implements UserCatalogueServiceInterface {

    @Autowired
    private UserCatalogueRepository userCatalogueRepository; // データベース操作を行うリポジトリ

    private static final Logger logger = LoggerFactory.getLogger(UserCatalogueService.class); // ロガーの設定

    @Override
    public List<UserCatalogue> getAll(Map<String, String[]> parameters) {
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null; // ソート条件
        Sort sort = createSort(sortParam); // BaseServiceからソートを生成

        String keyword = FilterParameter.filterKeyword(parameters);
        Map<String, String> simpleFilters = FilterParameter.filterSimple(parameters);
        Map<String, Map<String, String>> filterComplex = FilterParameter.filterComplex(parameters);

        Specification<UserCatalogue> specs = Specification.where(
            BaseSpecification.<UserCatalogue>keyword(keyword, "name"))
            .and(BaseSpecification.<UserCatalogue>whereSpec(simpleFilters))
            .and(BaseSpecification.<UserCatalogue>complexWhereSpec(filterComplex));

        return userCatalogueRepository.findAll(specs, sort);
    }

    /**
     * ページネーション処理
     * - パラメータ（page, perpage, sort）を取得し、Pageableオブジェクトを作成する
     * - findAll(Pageable) を使ってページングされたデータを取得する
     */
    @Override
    public Page<UserCatalogue> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1; // ページ番号（デフォルト1）
        int perPage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 20; // 1ページあたりの件数（デフォルト20）
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null; // ソート条件
        Sort sort = createSort(sortParam); // BaseServiceからソートを生成

        String keyword = FilterParameter.filterKeyword(parameters);
        Map<String, String> simpleFilters = FilterParameter.filterSimple(parameters);
        Map<String, Map<String, String>> filterComplex = FilterParameter.filterComplex(parameters);

        logger.info("keyword: "+ keyword);
        logger.info("simpleFilters: "+ simpleFilters);
        logger.info("filterComplex: " + filterComplex);

        Specification<UserCatalogue> specs = Specification.where(
            BaseSpecification.<UserCatalogue>keyword(keyword, "name"))
            .and(BaseSpecification.<UserCatalogue>whereSpec(simpleFilters))
            .and(BaseSpecification.<UserCatalogue>complexWhereSpec(filterComplex));

        Pageable pageable = PageRequest.of(page - 1, perPage, sort); // ページ情報を設定

        return userCatalogueRepository.findAll(specs, pageable); // ページング付き検索を実行
    }

    /**
     * 新規登録処理
     * - トランザクション管理付き（@Transactional）
     * - リクエストデータからエンティティを生成し、保存
     */
    @Override
    @Transactional
    public UserCatalogue create(StoreRequest request) {
        try {
            UserCatalogue payload = UserCatalogue.builder()
                    .name(request.getName()) // 名前を設定
                    .publish(request.getPublish()) // 公開設定を設定
                    .build();

            return userCatalogueRepository.save(payload); // データを保存
        } catch (Exception e) {
            logger.error("error {}", e.getMessage()); // エラー内容をログに出力
            throw new RuntimeException("トランザクションに失敗しました: " + e.getMessage()); // 例外を再スロー
        }
    }

    /**
     * 更新処理
     * - 指定IDのデータを取得し、存在しない場合はEntityNotFoundExceptionをスロー
     * - 更新後のデータを保存
     */
    @Override
    @Transactional
    public UserCatalogue update(Long id, UpdateRequest request) {

        // 対象データを取得。存在しなければ例外をスロー
        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("対象データが存在しません"));

        // 既存データを基に新しい値を上書き（Builderパターン）
        UserCatalogue payload = userCatalogue.toBuilder()
                .name(request.getName())
                .publish(request.getPublish())
                .build();

        // 更新データを保存
        return userCatalogueRepository.save(payload);
    }
}
