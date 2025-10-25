package dvn.local.dvnjs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.config.JwtConfig;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import dvn.local.dvnjs.modules.users.repositories.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;

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

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

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
        } catch (SignatureException e) {
            return false;
        } 
    }

    /**
     * 署名検証に用いる Key を生成。
     * （注）コンストラクタで作った key と生成方法が異なる点に注意。
     */
    public Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(keyBytes));
    }

    /**
     * トークンが期限切れかどうかを返す。
     * @return 期限切れなら true、まだ有効なら false
     */
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /**
     * 発行者(iss) が設定と一致するかを返す。
     */
    public boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return jwtConfig.getIssuer().equals(tokenIssuer);
    }

    /**
     * トークンがブラックリストに登録されているかを確認する。
     * 
     * @param token チェック対象のJWTトークン
     * @return ブラックリストに存在する場合は true、存在しない場合は false
     */
    public boolean isBlackListedToken(String token) {
        // BlacklistedTokenRepository を使用してDB内に該当トークンがあるか確認
        return blacklistedTokenRepository.existsByToken(token);
    }

    /**
     * すべてのクレーム（Claims）を取得する内部ヘルパー。
     */
    public Claims getAllClaimsFromToken(String token) {
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
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
}
