package dvn.local.dvnjs.modules.users.requests.UserCatalogue;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// ログインリクエストを表すDTOクラス
// クライアントから送信されたメールアドレスとパスワードを受け取るために使用
@Data
public class StoreRequest {
    
    // メールアドレス（ログインIDとして使用）
    @NotBlank(message = "空で禁止") // 空文字やnullは禁止
    private String email;

    // パスワード
    @NotBlank(message = "状態が空禁止") // 空やnullを禁止
    @Min(value = 0, message="0からの値を入力してください。")
    @Max(value = 2, message="2の値を入力してください。")
    private String publish;
}
