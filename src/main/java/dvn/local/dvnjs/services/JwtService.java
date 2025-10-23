package dvn.local.dvnjs.services;

import org.springframework.stereotype.Service;

import dvn.local.dvnjs.config.JwtConfig;
import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;

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

/**
 * 【概要】
 * JwtService は JWT の生成・解析・検証を行うサービス層クラスです。
 *
 * 主な提供機能：
 *  - generateToken      : ユーザーID/メールから署名付きJWTを生成
 *  - getUserIdFromJwt   : JWTのSubject（ここでは userId）を取得
 *  - getEmailFromJwt    : JWTのカスタムクレーム "email" を取得
 *  - isValidToken       : 形式・署名・有効期限・発行者・ユーザー整合性の総合検証
 *  - isTokenFormatValid : 3分割（header.payload.signature）の形式確認
 *  - isSignatureValid   : 署名の妥当性確認（秘密鍵で検証）
 *  - isTokenExpired     : 有効期限切れ判定
 *  - isIssuerToken      : 発行者(iss)の一致確認
 *
 * 設計メモ：
 *  - 署名鍵は HMAC-SHA 系の鍵（HS512）を使用
 *  - 例外時は適切にログに出力し、呼び出し側に true/false を返す流儀
 */
@Service // サービス層（ビジネスロジック担当）であることを示す
public class JwtService {

    // JWTに関する設定（シークレットキー、期限、発行者 など）
    private final JwtConfig jwtConfig;

    // 署名・検証に使う秘密鍵
    private final Key key;

    // ロガー（※クラス名は JwtService を使うのが自然）
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    /**
     * コンストラクタ：設定を受け取り、署名用Keyを初期化
     * @param jwtConfig シークレットキーや有効期限等の設定
     */
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        // シークレット文字列を Base64 でエンコードして HMAC-SHA 用の Key を生成
        // （注）getSigningKey() でも鍵を作るが、そこでは生バイトを使用している点に注意
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
    }

    /**
     * JWT を生成する。
     * @param userId 対象ユーザーID（Subjectに入れる）
     * @param email  カスタムクレーム "email"
     * @return 署名済みのJWT（HS512）
     */
    public String generateToken(Long userId, String email) {
        Date now = new Date(); // 発行時刻
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime()); // 有効期限 = 現在 + 設定値

        // ビルダーで JWT を組み立て、署名して返却
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // Subject（ここでは userId）
                .claim("email", email)               // カスタムクレーム
                .setIssuer(jwtConfig.getIssuer())    // 発行者(iss)
                .setIssuedAt(now)                    // 発行時刻(iat)
                .setExpiration(expiryDate)           // 期限(exp)
                .signWith(key, SignatureAlgorithm.HS512) // HS512で署名
                .compact();
    }

    /**
     * JWT から Subject（= userId）を取り出す。
     * 署名検証に成功しないと Claims は取得できない。
     */
    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)   // 署名検証に使用
                .build()
                .parseClaimsJws(token) // 成功すると署名OK + 期限内
                .getBody();
        return claims.getSubject();
    }

    /**
     * JWT からカスタムクレーム "email" を取り出す。
     */
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    /**
     * トークンの総合検証。
     * 形式・署名・有効期限・発行者・ユーザー一致の観点で false/true を返す。
     *
     * @param token       検証対象のJWT
     * @param userDetails 照合用ユーザー（Spring Security）
     * @return 妥当なら true、いずれか不正なら false
     */
    public boolean isValidToken(String token, UserDetails userDetails) {
        try {
            // 1) 形式（header.payload.signature の3部構成）
            if (!isTokenFormatValid(token)) {
                logger.error("トークンの形式が不正です。");
                return false;
            }

            // 2) 署名妥当性（鍵で検証）
            if (!isSignatureValid(token)) {
                logger.error("トークン署名の検証に失敗しました。");
                return false;
            }

            // 3) 期限切れ（exp）の確認
            //    ※ isTokenExpired は「期限切れなら true」を返す実装
            if (isTokenExpired(token)) {
                logger.error("トークンの有効期限が切れています。");
                return false;
            }

            // 4) 発行者(iss)の確認
            if (!isIssuerToken(token)) {
                logger.error("トークンの発行者が不正です。");
                return false;
            }

            // 5) ユーザー一致（email と Spring Security の username を突合）
            final String emailFromToken = getEmailFromJwt(token);
            if (!emailFromToken.equals(userDetails.getUsername())) {
                logger.error("トークンのユーザー情報が一致しません。");
                return false;
            }

            return true;
        } catch (Exception e) {
            // 予期しない例外（パース失敗等）
            logger.error("トークン検証に失敗しました。", e);
        }
        return false;
    }

    /**
     * トークンの形式が「ヘッダ.ペイロード.署名」の3分割になっているかをざっくり確認。
     */
    public boolean isTokenFormatValid(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            return tokenParts.length == 3;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * トークン署名の妥当性を検証（秘密鍵で parse 成功するか）。
     * 期限切れ等の一般的なJWT例外もここで拾ってログ出力。
     */
    public boolean isSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token); // 成功すれば署名OK
            return true;
        } catch (MalformedJwtException e) {
            logger.error("JWTトークンの形式が不正です。", e);
        } catch (ExpiredJwtException e) {
            logger.error("JWTトークンの有効期限が切れています。", e);
        } catch (UnsupportedJwtException e) {
            logger.error("サポートされていないJWTトークンです。", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWTトークンが空または不正です。", e);
        }
        return false;
    }

    /**
     * 署名検証に用いる Key を生成。
     * （注）コンストラクタで作った key と生成方法が異なる点に注意。
     */
    public Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * トークンが期限切れかどうかを返す。
     * @return 期限切れなら true、まだ有効なら false
     */
    public boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    /**
     * 発行者(iss) が設定と一致するかを返す。
     */
    public boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return jwtConfig.getIssuer().equals(tokenIssuer);
    }

    /**
     * すべてのクレーム（Claims）を取得する内部ヘルパー。
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    /**
     * 汎用クレーム取得ヘルパー。
     * @param claimsResolver Claims から必要な値を取り出す関数
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
}
