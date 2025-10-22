package dvn.local.dvnjs.services;

import org.springframework.stereotype.Service;

import dvn.local.dvnjs.config.JwtConfig;
import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.controllers.AuthController;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Service // サービス層を示すアノテーション。ビジネスロジックを担当するクラス。
public class JwtService {

    private final JwtConfig jwtConfig; // JWTの設定情報を保持するクラス
    private final Key key; // トークン署名に使う秘密鍵

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

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
                .setIssuer(jwtConfig.getIssuer())
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

    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        try {

            //
            if (!isTokenFormatValid(token)) {
                logger.error("トークンは定義正くありません。");
                return false;
            }

            //
            if (!isSignatureValid(token)) {
                logger.error("トークンは無効です。");
                return false;
            }

            //
            if (!isTokenExpired(token)) {
                logger.error("トークンもう切れです。");
                return false;
            }

            //
            if (!isIssuerToken(token)) {
                logger.error("トークンの生成元は無効です。");
                return false;
            }
            
            //
            final String emailFromToken = getEmailFromJwt(token);
            if (!emailFromToken.equals(userDetails.getUsername())) {
                logger.error("トークンは無効です。");
                return false;
            } 
            

            return true;
        } catch (Exception e) {
            logger.error("トークン確認できませんでした。", e.getMessage());
        }

        return false;
    }
    
    private boolean isTokenFormatValid(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            return tokenParts.length == 3;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSignatureValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);

            return true;
        } catch (MalformedJwtException e) {
            // トークンの形式が正しくない場合
            logger.error("JWTトークンの形式が不正です。", e.getMessage());
        } catch (ExpiredJwtException e) {
            // トークンの有効期限が切れた場合
            logger.error("JWTトークンの有効期限が切れています。", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // サポートされていないトークン
            logger.error("サポートされていないJWTトークンです。", e.getMessage());
        } catch (IllegalArgumentException e) {
            // トークンが空または不正な場合
            logger.error("JWTトークンが空または不正です。", e.getMessage());
        }

        return false;
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return jwtConfig.getIssuer().equals(tokenIssuer);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
}

