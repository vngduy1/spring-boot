package dvn.local.dvnjs.modules.users.resources;

// ログインレスポンスを表すUserResourceクラス
// クライアントへトークンとユーザー情報を返すために使用される
public class LoginResource {
    
    // JWTなどの認証トークン
    private final String token;

    // ログインしたユーザーの情報
    private final UserResource user;

    // コンストラクタ（トークンとユーザー情報を受け取る）
    public LoginResource(String token, UserResource user) {
        this.token = token;
        this.user = user;
    }

    // トークンを取得
    public String getToken() {
        return token;
    }

    // ユーザー情報を取得
    public UserResource getUser() {
        return user;
    }
}
