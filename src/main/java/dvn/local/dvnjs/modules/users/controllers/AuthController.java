package dvn.local.dvnjs.modules.users.controllers;

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

import dvn.local.dvnjs.modules.users.requests.BlacklistTokenRequest;
import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;
import dvn.local.dvnjs.modules.users.services.impl.BlackListService;
import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import dvn.local.dvnjs.resources.ErrorResource;
import dvn.local.dvnjs.resources.MessageResource;

import jakarta.validation.Valid;

@Validated
@RestController // REST APIのコントローラークラスであることを示すアノテーション
@RequestMapping("api/v1/auth") // このクラス内のエンドポイントの共通パスを定義
public class AuthController {

    // UserServiceを使って認証処理を実行するための依存オブジェクト
    private final UserServiceInterface userService;

    // BlackListServiceクラスを自動的に注入する（DI：依存性注入）
    // 他のクラスでBlackListServiceの機能を利用できるようにする
    @Autowired
    private BlackListService blackListService;

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
        // LoginResourceのインスタンスが返ってきた場合（token + user情報）
        if (result instanceof LoginResource loginResource) {
            // HTTPステータス200（OK）でレスポンスを返す
            return ResponseEntity.ok(loginResource);
        }

        // --- 認証失敗時のレスポンス ---
        // ErrorResourceのインスタンスが返ってきた場合（バリデーションまたは認証エラー）
        if (result instanceof ErrorResource errorResource) {
            // HTTPステータス422（Unprocessable Entity）でレスポンスを返す
            return ResponseEntity.unprocessableEntity().body(errorResource);
        }

        // --- 想定外のエラーが発生した場合 ---
        // 例えばresultがどちらの型でもない場合、HTTP 500を返す
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("認証処理中にエラーが発生しました。");
    }

    @PostMapping("blacklisted_tokens") 
    // POSTメソッドで /api/v1/auth/blacklisted_tokens にアクセスされたときに実行される。
    // ブラックリストにトークンを追加するためのAPIエンドポイント。
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        try {
            // BlackListServiceを呼び出してトークンを登録
            Object result = blackListService.create(request);

            // 成功した場合、HTTPステータス200(OK)を返す
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 予期しないエラーが発生した場合、サーバーエラー(500)を返す
            return ResponseEntity.internalServerError().body(
                    new MessageResource("ネットワークエラーが発生しました。"));
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
            Object message = blackListService.create(request);

            // 成功時にHTTPステータス200(OK)を返す
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            // 例外が発生した場合、サーバーエラー(500)を返す
            return ResponseEntity.internalServerError()
                    .body(new MessageResource("ネットワークエラーが発生しました。"));
        }
    }

}
