package dvn.local.dvnjs.modules.users.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

// ログインリクエストを表すDTOクラス
// クライアントから送信されたメールアドレスとパスワードを受け取るために使用
public class LoginRequest {
    
    // メールアドレス（ログインIDとして使用）
    @NotBlank(message = "メールアドレスは空でございます。") // 空文字やnullは禁止
    @Email(message = "メールアドレスの形式が正しくありません") // メール形式をチェック
    @Size(max = 100, message = "メールアドレスは100文字以内で入力してください。") // 長さ制限
    private String email;

    // パスワード
    @NotBlank(message = "パスワードは必須項目です。") // 空やnullを禁止
    @Size(min = 8, max = 20, message = "パスワードは8文字以上20文字以内で入力してください。") // 長さ制限
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=]+$",
             message = "パスワードは英字と数字を含める必要があります。") // 英字＋数字を必須にする
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
