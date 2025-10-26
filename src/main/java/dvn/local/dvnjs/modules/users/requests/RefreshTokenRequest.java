package dvn.local.dvnjs.modules.users.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    // トークンが空または null の場合はエラーメッセージを表示する
    @NotBlank(message = "リフレッシュトークンは空です。入力してください。")
    private String refreshToken;

}
