package dvn.local.dvnjs.modules.users.requests;

// ログインリクエストを表すDTOクラス
// クライアントから送信されたメールアドレスとパスワードを受け取るために使用
public class LoginRequest {
    
    // メールアドレス（ログインIDとして使用）
    private String email;

    // パスワード
    private String password;

    // メールアドレスを取得
    public String getEmail() {
        return email;
    }

    // メールアドレスを設定
    public void setEmail(String email) {
        this.email = email;
    }

    // パスワードを取得
    public String getPassword() {
        return password;
    }

    // パスワードを設定
    public void setPassword(String password) {
        this.password = password;
    }
}
