package dvn.local.dvnjs.modules.users.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ブラックリスト登録用のリクエストクラス。
 * 
 * クライアントから送信された JWT トークンを受け取るために使用される。
 * バリデーションアノテーションを使用して、空文字や null の入力を防ぐ。
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlacklistTokenRequest {
    
    // トークンが空または null の場合はエラーメッセージを表示する
    @NotBlank(message = "トークンは空です。入力してください。")
    private String token;
}
