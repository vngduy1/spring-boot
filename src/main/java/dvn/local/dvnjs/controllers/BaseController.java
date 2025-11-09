package dvn.local.dvnjs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;
import dvn.local.dvnjs.modules.users.services.interfaces.BaseServiceInterface;
import dvn.local.dvnjs.resources.ApiResource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

// 基底コントローラークラス
public abstract class BaseController<
    E,
    R,
    C,
    U,
    Rp extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>
>{
    protected final BaseServiceInterface<E, C, U> service; // サービス層の依存関係
    protected final BaseMapper<E, R, C, U> mapper; // マッパーの依存関係
    protected final Rp repository;

    // コンストラクタインジェクションでサービスとマッパーを初期化
    public BaseController(
        BaseServiceInterface<E, C, U> service,
        BaseMapper<E, R, C, U> mapper,
        Rp repository
    ) {
        this.service = service; // サービス層の初期化
        this.mapper = mapper; // マッパーの初期化
        this.repository = repository; // リポジトリの初期化
    }
    
    // 【GET】/api/v1/{resource}/list
    @GetMapping("/list")
    public ResponseEntity<?> list(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap(); // クエリパラメータを取得
        List<E> entities = service.getAll(parameters); // サービス層で全件取得
        List<R> resource = mapper.toList(entities); // エンティティをリソースに変換
        ApiResource<List<R>> response = ApiResource.ok(resource, "SUCCESS"); // ApiResource形式でレスポンスを作成
        return ResponseEntity.ok(response); // レスポンスを返却
    }
    
    // 【GET】/api/v1/user_catalogues
    @GetMapping
    public ResponseEntity<?> pagination(HttpServletRequest request) {
        // クエリパラメータをMap形式で取得
        Map<String, String[]> parameters = request.getParameterMap();
        // サービス層でページング処理を実行
        Page<E> entities = service.paginate(parameters);
        // エンティティをリソース形式に変換
        Page<R> resource = mapper.toResourcePage(entities);
        // API共通レスポンス形式で返却
        ApiResource<Page<R>> response = ApiResource.ok(resource, "SUCCESS");
        return ResponseEntity.ok(response); // レスポンスを返却
    }
    
    // 【POST】/api/v1/user_catalogues
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody C request) {
        try {
            E entities = service.create(request);     // サービス層で新規作成
            R resource = mapper.tResource(entities);  // エンティティをリソース形式に変換
            // API共通レスポンスで返却
            ApiResource<R> response = ApiResource.ok(resource, "定義書が正常に追加されました。");
            return ResponseEntity.ok(response);  // レスポンスを返却
        } catch (Exception e) {
            String message ="Có lỗi xảy ra trong quá trình tạo mới" + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error(
                    "INTERNAL_SERVER_ERROR",
                    message,
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }

    }
    
    // 【PUT】/api/v1/user_catalogues/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable Long id,
            @Valid @RequestBody U request) {

        try {
            E entities = service.update(id, request);  // サービス層で更新処理
            R resource = mapper.tResource(entities);   // エンティティをリソース形式に変換
            ApiResource<R> response = ApiResource.ok(resource, "定義書が正常に更新されました。");
            return ResponseEntity.ok(response);        // レスポンスを返却
        } catch (EntityNotFoundException e) {
            // 対象データが存在しない場合
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error(
                    "NOT_FOUND",
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
                )
            );
        } catch (Exception e) {
            // その他のエラー（サーバーエラー）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error(
                    "INTERNAL_SERVER_ERROR",
                    "Có lỗi xảy ra trong quá trình cập nhật",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }
    }
    
    // 【GET】/api/v1/user_catalogues/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        // 指定IDのユーザー定義書を取得
        E entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserCatalogue with ID " + id + " not found"));

        R resource = mapper.tResource(entity); // エンティティをリソースに変換
        // 返却用レスポンスを作成
        ApiResource<R> response = ApiResource.ok(resource, "SUCCESS");
        return ResponseEntity.ok(response);
    }

    // 【DELETE】/api/v1/user_catalogues/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id); // サービス層で削除処理を実行
            return ResponseEntity.ok(
                ApiResource.message("定義書が正常に削除されました。", HttpStatus.OK)); // 成功レスポンスを返却
        } catch (EntityNotFoundException e) {
            // 対象データが存在しない場合
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error(
                    "NOT_FOUND",
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
                )
            );
        } catch (Exception e) {
            // その他のエラー（サーバーエラー）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error(
                    "INTERNAL_SERVER_ERROR",
                    "Có lỗi xảy ra trong quá trình xóa",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }
    }

    // 【DELETE】/api/v1/user_catalogues
    @DeleteMapping
    public ResponseEntity<?> deleteMany(@RequestBody List<Long> ids) {
        try {
            service.deleteMultipleEntity(ids); // サービス層で複数削除処理を実行
            return ResponseEntity.ok(
                ApiResource.message("選択された定義書が正常に削除されました。", HttpStatus.OK)); // 成功レスポンスを返却
        } catch (EntityNotFoundException e) {
            // 対象データが存在しない場合
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error(
                    "NOT_FOUND",
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
                )
            );
        } catch (Exception e) {
            // その他のエラー（サーバーエラー）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error(
                    "INTERNAL_SERVER_ERROR",
                    "Có lỗi xảy ra trong quá trình xóa",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }
    }
    

}
