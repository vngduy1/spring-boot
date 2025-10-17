package dvn.local.dvnjs.modules.users.dtos;

// ログインレスポンスを表すDTOクラス
// クライアントへトークンとユーザー情報を返すために使用される
public class LoginResponse {
    
    // JWTなどの認証トークン
    private final String token;

    // ログインしたユーザーの情報
    private final UserDTO user;

    // コンストラクタ（トークンとユーザー情報を受け取る）
    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    // トークンを取得
    public String getToken() {
        return token;
    }

    // ユーザー情報を取得
    public UserDTO getUser() {
        return user;
    }
}
