package dvn.local.dvnjs.modules.users.controllers;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.entities.User;
import dvn.local.dvnjs.modules.users.resources.UserResource;
import dvn.local.dvnjs.modules.users.repositories.UserRepository;
import dvn.local.dvnjs.resources.SuccessResource;

@RestController // このクラスがREST APIのコントローラーであることを示す
@RequestMapping("api/v1") // すべてのエンドポイントの共通パスのプレフィックスを定義
public class UserController {

    // ユーザー情報をデータベースから取得するためのリポジトリ
    @Autowired
    private UserRepository userRepository;

    // ロガーの設定
    // private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 現在のユーザー情報を取得するエンドポイント
     * 
     * @return ユーザー情報（id、email、name）を含むレスポンス
     * 
     * 実際の運用では、ログイン済みユーザーの情報（トークンから取得）を返すようにする。
     * 現在は「email」という固定値でデータを取得するテスト用実装。
     */
    @GetMapping("me") // POSTメソッドで /api/v1/me にアクセスされたときに実行される
    public ResponseEntity<?> me() {
        // String email = "admin@example.com";

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // --- ユーザーをメールアドレスで検索 ---
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    // ユーザーが存在しない場合に例外を投げる
                    new BadCredentialsException("ユーザーが存在しません。")
                );

        // --- レスポンス用のユーザー情報を作成 ---
        UserResource userResource = UserResource.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())    
        .phone(user.getPhone())
        .build();

        SuccessResource<UserResource> response = new SuccessResource<>("SUCCESS", userResource);

        // --- HTTPステータス200（OK）でユーザー情報を返す ---
        return ResponseEntity.ok(response);
    }
}
