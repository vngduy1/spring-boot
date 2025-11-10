package dvn.local.dvnjs.modules.users.requests.Permission;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// ログインリクエストを表すDTOクラス
// クライアントから送信されたメールアドレスとパスワードを受け取るために使用
@Data
public class UpdateRequest {
    
    // メールアドレス（ログインIDとして使用）
    @NotBlank(message = "空で禁止") // 空文字やnullは禁止
    private String name;

    // パスワード
    @NotNull(message = "状態が空禁止") // 空やnullを禁止
    @Min(value = 0, message="0からの値を入力してください。")
    @Max(value = 2, message="2の値を入力してください。")
    private Integer publish;
}
