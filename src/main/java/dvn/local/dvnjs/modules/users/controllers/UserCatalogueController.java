package dvn.local.dvnjs.modules.users.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import dvn.local.dvnjs.resources.ApiResource;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Validated
@RestController // このクラスがREST APIのコントローラーであることを示す
@RequestMapping("api/v1") // すべてのエンドポイントの共通URLプレフィックス
public class UserCatalogueController {
    
    // ロガーの設定
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // サービス層の依存関係を注入
    private final UserCatalogueServiceInterface userCatalogueService;

    // コンストラクタインジェクションでサービスを初期化
    public UserCatalogueController(UserCatalogueServiceInterface userCatalogueService) {
        this.userCatalogueService = userCatalogueService;
    }

    /**
     * 【GET】/api/v1/user_catalogues
     * 
     * ユーザー定義書の一覧をページネーション付きで取得する。
     * 
     * @param request ページング・ソートなどのパラメータを取得するためのリクエスト
     * @return ページング済みのUserCatalogueResourceリストをApiResource形式で返却
     */
    @GetMapping("/user_catalogues")
    public ResponseEntity<?> index(HttpServletRequest request) {
        // クエリパラメータをMap形式で取得
        Map<String, String[]> parameters = request.getParameterMap();

        // サービス層でページング処理を実行
        Page<UserCatalogue> userCatalogues = userCatalogueService.paginate(parameters);

        // エンティティをリソース形式に変換
        Page<UserCatalogueResource> userCataloguesResource = userCatalogues.map(userCatalogue -> UserCatalogueResource.builder()
            .id(userCatalogue.getId())
            .name(userCatalogue.getName())
            .publish(userCatalogue.getPublish())
            .build()
        );

        // API共通レスポンス形式で返却
        ApiResource<Page<UserCatalogueResource>> response = ApiResource.ok(userCataloguesResource, "SUCCESS");

        return ResponseEntity.ok(response);
    }
    
    /**
     * 【POST】/api/v1/user_catalogues
     * 
     * 新しいユーザー定義書を登録する。
     * 
     * @param request 登録用リクエストボディ（バリデーションあり）
     * @return 登録成功時のUserCatalogueResourceを返却
     */
    @PostMapping("/user_catalogues")
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request) {

        // サービス層で登録処理を実行
        UserCatalogue userCatalogue = userCatalogueService.create(request);

        // レスポンス用オブジェクトを生成
        UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish())
                .build();

        // API共通レスポンスで返却
        ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "定義書が正常に追加されました。");
        logger.info("success");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 【PUT】/api/v1/user_catalogues/{id}
     * 
     * 既存のユーザー定義書を更新する。
     * 
     * @param id 更新対象のID
     * @param request 更新内容
     * @return 更新結果のUserCatalogueResourceを返却
     * 
     * エラーハンドリング:
     * - EntityNotFoundException → 404 NOT FOUND
     * - その他のException → 500 INTERNAL SERVER ERROR
     */
    @PutMapping("/user_catalogues/{id}")
    public ResponseEntity<?> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateRequest request) {

        try {
            // 更新処理を実行
            UserCatalogue userCatalogue = userCatalogueService.update(id, request);

            // レスポンス変換
            UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
                    .id(userCatalogue.getId())
                    .name(userCatalogue.getName())
                    .publish(userCatalogue.getPublish())
                    .build();

            ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "定義書が正常に更新されました。");
            logger.info("success");
            return ResponseEntity.ok(response);

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
                    "Có lỗi xảy ra trong quá trình cập nhật", // ベトナム語のままでもOK
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }
    }
}
