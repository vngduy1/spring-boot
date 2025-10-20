package dvn.local.dvnjs.modules.users.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import dvn.local.dvnjs.services.BaseService;
import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;
import dvn.local.dvnjs.modules.users.resources.UserResource;
import dvn.local.dvnjs.modules.users.entities.User;
import dvn.local.dvnjs.modules.users.repositories.UserRepository;
import dvn.local.dvnjs.resources.ErrorResource;
import dvn.local.dvnjs.services.JwtService;

@Service // サービス層を表すアノテーション。ビジネスロジックを担当するクラス。
public class UserService extends BaseService implements UserServiceInterface {

    // ログ出力用のロガー定義
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtService jwtService; // JWTトークン生成サービス

    @Autowired
    private PasswordEncoder passwordEncoder; // パスワード暗号化・照合用

    @Autowired
    private UserRepository userRepository; // ユーザー情報を操作するリポジトリ

    /**
     * ユーザー認証処理を行うメソッド
     * 
     * @param request ログインリクエスト（メールアドレスとパスワードを含む）
     * @return 認証成功時は LoginResource（トークン＋ユーザー情報）、
     *         認証失敗時は ErrorResource（エラーメッセージ）を返す
     */
    @Override
    public Object authenticate(LoginRequest request) {
        try {
            // --- 入力されたメールアドレスでユーザーを検索 ---
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                    // ユーザーが存在しない場合、BadCredentialsExceptionを投げる
                    new BadCredentialsException("メールアドレスあるいはパスワードが正しくありません。")
                );

            // --- パスワードの一致確認 ---
            // 入力されたパスワードとDB上の暗号化済みパスワードを比較
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // 一致しない場合も同じ例外を投げる（セキュリティのため詳細は区別しない）
                throw new BadCredentialsException("メールアドレスあるいはパスワードが正しくありません。");
            }

            // --- JWTトークン生成 ---
            String token = jwtService.generateToken(user.getId(), user.getEmail());

            // --- レスポンス用のユーザー情報を作成 ---
            UserResource userResource = new UserResource(
                user.getId(),
                user.getEmail(),
                user.getName()
            );

            // --- 成功時のレスポンス（トークン＋ユーザー情報）を返す ---
            return new LoginResource(token, userResource);

        } catch (BadCredentialsException e) {
            // --- 認証失敗時の処理 ---
            // エラーログを出力
            logger.error("認証処理中にエラーが発生しました。", e.getMessage());

            // --- エラーレスポンスを作成 ---
            Map<String, String> errors = new HashMap<>();
            errors.put("message", e.getMessage());

            ErrorResource errorResource = new ErrorResource(
                "認証処理中にエラーが発生しました。",
                errors
            );

            // --- エラー内容を返す ---
            return errorResource;
        }
    }
}
