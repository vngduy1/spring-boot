package dvn.local.dvnjs.modules.users.resources;

import lombok.Data;
import lombok.RequiredArgsConstructor;

// ログインレスポンスを表すUserResourceクラス
// クライアントへトークンとユーザー情報を返すために使用される
@Data
@RequiredArgsConstructor
public class TokenResource {
    
    // JWTなどの認証トークン
    private final String token;

}
