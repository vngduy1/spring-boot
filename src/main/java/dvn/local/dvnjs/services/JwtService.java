package dvn.local.dvnjs.services;

import org.springframework.stereotype.Service;

import dvn.local.dvnjs.config.JwtConfig;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

@Service // サービス層を示すアノテーション。ビジネスロジックを担当するクラス。
public class JwtService {

    private final JwtConfig jwtConfig; // JWTの設定情報を保持するクラス
    private final Key key; // トークン署名に使う秘密鍵

    // コンストラクタ。JwtConfigを注入し、秘密鍵を生成
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        // 秘密鍵をBase64エンコードしてHMAC-SHAアルゴリズム用のKeyを生成
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
    }

    // JWTトークンを生成するメソッド
    public String generateToken(Long userId, String email) {
        Date now = new Date(); // 現在時刻
        // 有効期限を現在時刻 + 設定値で計算
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());

        // トークンを構築して返す
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // サブジェクト（ここではユーザーID）
                .claim("email", email) // カスタムクレームとしてメールアドレスを追加
                .setIssuedAt(now) // 発行日時
                .setExpiration(expiryDate) // 有効期限
                .signWith(key, SignatureAlgorithm.HS512) // HS512アルゴリズムで署名
                .compact(); // トークン文字列を生成
    }
    
    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
    
        return claims.getSubject();
    }
    

}

