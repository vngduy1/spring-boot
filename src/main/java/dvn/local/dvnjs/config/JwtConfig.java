package dvn.local.dvnjs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // Spring Bean として登録（他のクラスから @Autowired で利用可能）
public class JwtConfig {

    // application.properties の "jwt.secret" を読み込む
    @Value("${jwt.secret}")
    private String secretKey;

    // application.properties の "jwt.expiration" を読み込む（{}の入れ子は不要）
    @Value("${jwt.expiration}")
    private Long expirationTime;

    // 秘密鍵を取得するメソッド
    public String getSecretKey() {
        return secretKey;
    }

    // 有効期限（ミリ秒）を取得するメソッド
    public Long getExpirationTime() {
        return expirationTime;
    }
}
