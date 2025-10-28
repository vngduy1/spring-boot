package dvn.local.dvnjs.modules.users.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

// ログインレスポンスを表すUserResourceクラス
// クライアントへトークンとユーザー情報を返すために使用される
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RefreshTokenResource {
    
    // JWTなどの認証トークン
    private final String token;
    //リフレッシュトークン
    private String refreshToken;

}
