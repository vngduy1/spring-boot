package dvn.local.dvnjs.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.helpers.FilterParameter;
import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.specifications.BaseSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service // 共通のサービスクラスとしてSpringに登録
public abstract class BaseService<
        T, 
        M extends BaseMapper<T, ?, C, U>, 
        C, 
        U,
        R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>
    > {

    // 抽象メソッド：サブクラスで実装する必要がある
    protected abstract String[] getSearchFields();
    protected abstract R getRepository();
    protected abstract M getMapper();

    // 共通の全件取得メソッド
    public List<T> getAll(Map<String, String[]> parameters) {
        Sort sort = parseSort(parameters); // ソート条件を解析
        Specification<T> specs = buildSpecification(parameters, getSearchFields());
        return getRepository().findAll(specs, sort);
    }

    public Page<T> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1; // ページ番号（デフォルト1）
        int perPage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 20; // 1ページあたりの件数（デフォルト20）
        Sort sort = parseSort(parameters); // ソート条件を解析
        Specification<T> specs = buildSpecification(parameters, getSearchFields());

        Pageable pageable = PageRequest.of(page - 1, perPage, sort); // ページ情報を設定
        return getRepository().findAll(specs, pageable); // ページング付き検索を実行
    }
    
    // 共通の新規登録メソッド
    @Transactional
    public T create(C request) {
        T payload = getMapper().toEntity(request); // リクエストからエンティティを生成
        return getRepository().save(payload); // データを保存
    }

    // 共通の更新メソッド
    @Transactional
    public T update(Long id, U request) {
        // 対象データを取得。存在しなければ例外をスロー
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("対象データが存在しません"));
        // リクエストデータでエンティティを更新
        getMapper().updateEntityFromResource(request, entity);
        // 更新データを保存
        return getRepository().save(entity);
    }

    // 共通の削除メソッド
    @Transactional
    public Boolean delete(Long id) {
        getRepository().findById(id).orElseThrow(() -> new EntityNotFoundException("対象データが存在しません"));
        getRepository().deleteById(id); // データを削除
        return true; // 論理削除の場合、削除後のエンティティを返す
    }

    // 共通の複数削除メソッド
    @Transactional
    public Boolean deleteMultipleEntity(List<Long> ids) {
        List<T> entities = getRepository().findAllById(ids);
        if(entities.size() != ids.size()) {
            throw new EntityNotFoundException("削除対象のデータが存在しません");
        }
        getRepository().deleteAll(entities);
        return true; // 削除成功
    }

    // パラメータからソート条件を解析してSortオブジェクトを生成
    protected Sort parseSort(Map<String, String[]> parameters) {
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null; // ソート条件
        return createSort(sortParam);
    }

    // 共通のSpecification生成メソッド
    protected Specification<T> buildSpecification(Map<String, String[]> parameters, String[] searchField) {
        String keyword = FilterParameter.filterKeyword(parameters); // キーワード抽出
        // シンプルフィルターと複雑フィルターを抽出
        Map<String, String> simpleFilters = FilterParameter.filterSimple(parameters);
        // 複雑フィルターを抽出
        Map<String, Map<String, String>> filterComplex = FilterParameter.filterComplex(parameters);
        // Specificationを組み立てる
        Specification<T> specs = Specification.where(
                BaseSpecification.<T>keyword(keyword, searchField))
                .and(BaseSpecification.<T>whereSpec(simpleFilters))
                .and(BaseSpecification.<T>complexWhereSpec(filterComplex));
        return specs;
    }

    /**
     * ソート条件を生成するメソッド
     * 
     * @param sortParam 例: "name,asc" または "createdAt,desc"
     * @return Sort オブジェクト（Spring Data用）
     * 
     * 処理の流れ:
     * ① sortParam が null または 空 の場合 → id 降順でソート
     * ② sortParam を「,」で分割し、フィールド名とソート方向を取得
     * ③ asc / desc に応じて Sort オブジェクトを返す
     */
    protected Sort createSort(String sortParam) {
        // ソートパラメータが指定されていない場合、id降順
        if (sortParam == null || sortParam.isEmpty()) {
            return Sort.by(Sort.Order.desc("id"));
        }

        // 例: "name,asc" → ["name", "asc"]
        String[] parts = sortParam.split(",");
        String field = parts[0];
        String sortDirection = (parts.length > 1) ? parts[1] : "asc";

        // 昇順 / 降順を判定してSortオブジェクトを生成
        if ("desc".equalsIgnoreCase(sortDirection)) {
            return Sort.by(Sort.Order.desc(field));
        } else {
            return Sort.by(Sort.Order.asc(field));
        }
    }
}
