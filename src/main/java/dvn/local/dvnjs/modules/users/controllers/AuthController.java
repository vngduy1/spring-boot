package dvn.local.dvnjs.modules.users.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.entities.RefreshToken;
import dvn.local.dvnjs.modules.users.repositories.RefreshTokenRepository;
import dvn.local.dvnjs.modules.users.requests.BlacklistTokenRequest;
import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.requests.RefreshTokenRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;
import dvn.local.dvnjs.modules.users.resources.RefreshTokenResource;
import dvn.local.dvnjs.modules.users.services.impl.BlackListService;
import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import dvn.local.dvnjs.resources.ApiResource;
import dvn.local.dvnjs.resources.MessageResource;
import dvn.local.dvnjs.services.JwtService;

import jakarta.validation.Valid;

@Validated
@RestController // REST APIのコントローラークラスであることを示すアノテーション
@RequestMapping("api/v1/auth") // このクラス内のエンドポイントの共通パスを定義
public class AuthController {

    // UserServiceを使って認証処理を実行するための依存オブジェクト
    private final UserServiceInterface userService;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);


    // BlackListServiceクラスを自動的に注入する（DI：依存性注入）
    // 他のクラスでBlackListServiceの機能を利用できるようにする
    @Autowired
    private BlackListService blackListService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    // コンストラクタインジェクション（Springが自動でUserServiceを注入）
    public AuthController(UserServiceInterface userService) {
        this.userService = userService;
    }

    /**
     * ログイン処理を行うエンドポイント
     * 
     * @param request クライアントから送信されたログイン情報（メール・パスワード）
     * @return 
     *   認証成功 → HTTP 200 OK + LoginResource（トークンとユーザー情報）  
     *   認証失敗 → HTTP 422 Unprocessable Entity + ErrorResource（エラーメッセージ）  
     *   その他エラー → HTTP 500 Internal Server Error
     */
    @PostMapping("login") // POSTメソッドで /api/v1/auth/login にアクセスされたときに実行される
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // --- サービス層で認証処理を実行 ---
        Object result = userService.authenticate(request);

        // --- 認証成功時のレスポンス ---
        if (result instanceof LoginResource loginResource) {
            ApiResource<LoginResource> response = ApiResource.ok(loginResource, "Success");
            // HTTPステータス200（OK）でレスポンスを返す
            return ResponseEntity.ok(response);
        }

        // --- 認証失敗時のレスポンス ---
        // ErrorResourceのインスタンスが返ってきた場合（バリデーションまたは認証エラー）
        if (result instanceof ApiResource errorResource) {

            // HTTPステータス422（Unprocessable Entity）でレスポンスを返す
            return ResponseEntity.unprocessableEntity().body(errorResource);
        }

        // --- 想定外のエラーが発生した場合 ---
        // 例えばresultがどちらの型でもない場合、HTTP 500を返す
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("認証処理中にエラーが発生しました。");
    }

    // POSTメソッドで /api/v1/auth/blacklisted_tokens にアクセスされたときに実行される。
    // ブラックリストにトークンを追加するためのAPIエンドポイント。
    @PostMapping("blacklisted_tokens") 
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        try {
            // BlackListServiceを呼び出してトークンを登録
            Object result = blackListService.create(request);

            // 成功した場合、HTTPステータス200(OK)を返す
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            ApiResource<Void> errorResponse = ApiResource.<Void>builder().success(false).message("network error!")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("logout")
    // GETメソッドで /api/v1/auth/logout にアクセスされたときに実行される。
    // 認証トークンをブラックリストに登録し、ログアウト処理を行うAPIエンドポイント。
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Authorizationヘッダーから "Bearer " の部分を除いたトークンを取得
            String token = bearerToken.substring(7);

            // BlacklistTokenRequest オブジェクトを作成してトークンを設定
            BlacklistTokenRequest request = new BlacklistTokenRequest();
            request.setToken(token);

            // BlackListServiceを使ってトークンをブラックリストに登録
            blackListService.create(request);

            ApiResource<Void> successResponse = ApiResource.<Void>builder().success(true).message("logout successfully")
                    .status(HttpStatus.OK).build();
            // 成功時にHTTPステータス200(OK)を返す
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            ApiResource<Void> errorResponse = ApiResource.<Void>builder().success(false).message("network error!")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // リフレッシュトークンを使って新しいアクセストークン・リフレッシュトークンを発行するAPI
    // POST /api/v1/auth/refresh
    @PostMapping("refresh") 
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // クライアントから送信されたリフレッシュトークンを取得
        String refreshToken = request.getRefreshToken();
        logger.info("refreshToken"); // デバッグ用ログ（呼び出されたことを確認する）

        // 1. トークン形式や署名などが正しいかをチェック
        //    無効なトークンの場合は 401 (UNAUTHORIZED) を返す
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("リフレッシュトークンが無効です。"));
        }

        // 2. DBに保存しているリフレッシュトークンを検索
        //    → ブラックリスト対応や手動ログアウト済みトークンを弾くため
        Optional<RefreshToken> dbRefreshTokenOptional =
                refreshTokenRepository.findByRefreshToken(refreshToken);

        // 3. DBにトークンが存在する場合のみ、新しいトークンを発行する
        if (dbRefreshTokenOptional.isPresent()) {

            // DBに保存されているトークン情報を取得
            RefreshToken dbRefreshToken = dbRefreshTokenOptional.get();

            // ユーザーIDを取得
            Long userId = dbRefreshToken.getUserId();

            // 対象ユーザーのメールアドレスを取得
            String email = dbRefreshToken.getUser().getEmail();

            // 新しいアクセストークンを生成
            String newToken = jwtService.generateToken(userId, email, null);

            // 新しいリフレッシュトークンを生成
            String newRefreshToken = jwtService.generateRefreshToken(userId, email);

            // クライアントに新しいトークンセットを返却 (200 OK)
            return ResponseEntity.ok(
                    new RefreshTokenResource(newToken, newRefreshToken)
            );
        }

        // 4. ここまで来るのは想定外ケース
        //    例：DBにトークンが見つからない、DBアクセス異常など
        //    → サーバー側の問題として 500 エラーを返す
        return ResponseEntity
                .internalServerError()
                .body(new MessageResource("ネットワークエラーが発生しました。"));
    }

}
