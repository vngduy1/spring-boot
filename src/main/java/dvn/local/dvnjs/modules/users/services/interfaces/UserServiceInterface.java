package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.requests.LoginRequest;

/**
 * UserServiceInterface は、ユーザー認証やユーザー関連処理を定義するサービスインターフェース。
 * 
 * 実際の処理内容は、このインターフェースを実装したクラス（例：UserService）で実装される。
 */
public interface UserServiceInterface {

    /**
     * ユーザー認証処理を実行する。
     * 
     * @param request ログイン要求情報（メールアドレス・パスワードなど）
     * @return 認証結果オブジェクト（トークン情報やエラーメッセージなどを含む）
     */
    Object authenticate(LoginRequest request);
    
}