package dvn.local.dvnjs.modules.users.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * ブラックリスト登録用のリクエストクラス。
 * 
 * クライアントから送信された JWT トークンを受け取るために使用される。
 * バリデーションアノテーションを使用して、空文字や null の入力を防ぐ。
 */
public class BlacklistTokenRequest {
    
    // トークンが空または null の場合はエラーメッセージを表示する
    @NotBlank(message = "トークンは空です。入力してください。")
    private String token;

    // --- Getter / Setter ---

    /** 
     * トークンを取得する。
     * @return トークン文字列
     */
    public String getToken() {
        return token;
    }

    /** 
     * トークンを設定する。
     * @param token 登録するJWTトークン
     */
    public void setToken(String token) {
        this.token = token;
    }
}
