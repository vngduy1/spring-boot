package dvn.local.dvnjs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.config.JwtConfig;
import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.entities.RefreshToken;
import dvn.local.dvnjs.modules.users.repositories.BlacklistedTokenRepository;
import dvn.local.dvnjs.modules.users.repositories.RefreshTokenRepository;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

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

    //デバッグ用
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
                .claim("email", email) // カスタムクレーム
                .setIssuer(jwtConfig.getIssuer()) // 発行者(iss)
                .setIssuedAt(now) // 発行時刻(iat)
                .setExpiration(expiryDate) // 期限(exp)
                .signWith(key, SignatureAlgorithm.HS512) // HS512で署名
                .compact();
    }

    /**
     * リフレッシュトークンを生成し、データベースに保存または更新するメソッド。
     * 
     * <p>
     * ・ユーザーIDに紐づくトークンが既に存在する場合：  
     * 　　既存レコードを更新（トークン文字列と有効期限を再設定）  
     * ・存在しない場合：  
     * 　　新規レコードとして挿入
     * </p>
     * 
     * @param userId 対象ユーザーのID
     * @param email  対象ユーザーのメールアドレス（ログ用や将来拡張用）
     * @return 生成された新しいリフレッシュトークン文字列
     */
    public String generateRefreshToken(Long userId, String email) {

        // 現在時刻を取得
        Date now = new Date();

        // 有効期限を計算（設定ファイルの値を使用）
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpirationTime());

        // ランダムなUUIDを使って新しいリフレッシュトークンを生成
        String refreshToken = UUID.randomUUID().toString();

        // Date型 → LocalDateTime型へ変換（DB登録用）
        LocalDateTime localExpiryDate =
                expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // ユーザーIDに紐づく既存のリフレッシュトークンを検索
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);

        if (optionalRefreshToken.isPresent()) {
            // 既存レコードがある場合は更新処理を行う
            RefreshToken dbRefreshToken = optionalRefreshToken.get();
            dbRefreshToken.setRefreshToken(refreshToken);
            dbRefreshToken.setExpiryDate(localExpiryDate);

            // 更新後のトークン情報を保存
            refreshTokenRepository.save(dbRefreshToken);
        } else {
            // 既存レコードがない場合は新規作成
            RefreshToken insertToken = new RefreshToken();
            insertToken.setRefreshToken(refreshToken);
            insertToken.setExpiryDate(localExpiryDate);
            insertToken.setUserId(userId);

            // 新しいトークン情報を保存
            refreshTokenRepository.save(insertToken);
        }

        // クライアント側で利用するため、新しいトークン文字列を返却
        return refreshToken;
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
                    .parseClaimsJws(token); 
            return true;
        } catch (RuntimeException e) {
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
            Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 発行者(isIssuerToken) が設定と一致するかを返す。
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return null;
        } catch (JwtException e) {
            logger.error("トークンが無効です: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 汎用クレーム取得ヘルパー。
     * 
     * <p>
     * JWTトークンから任意のクレーム値を取得するための汎用メソッド。  
     * 例：トークンから「サブジェクト（ユーザーID）」や「有効期限」などを抽出する際に使用する。
     * </p>
     *
     * @param <T>          取得する値の型（例：String、Date、Booleanなど）
     * @param token        対象のJWTトークン文字列
     * @param claimsResolver Claimsオブジェクトから目的の値を取り出す関数
     * @return 取得したクレーム値。トークンがnullまたは不正な場合は null を返す。
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            // トークンを解析して全クレーム情報を取得
            final Claims claims = getAllClaimsFromToken(token);

            // 指定された関数（claimsResolver）を使って目的の値を抽出
            return claimsResolver.apply(claims);

        } catch (NullPointerException e) {
            // トークンがnullまたは解析に失敗した場合は null を返す
            return null;
        }
    }

    /**
     * リフレッシュトークンの有効性を検証するメソッド。
     * 
     * <p>
     * データベース上に該当トークンが存在し、かつ有効期限を過ぎていない場合に true を返す。  
     * 不正または期限切れのトークンである場合は false を返す。
     * </p>
     *
     * @param token チェック対象のリフレッシュトークン文字列
     * @return トークンが有効であれば true、無効または存在しない場合は false
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            // DBから該当のリフレッシュトークン情報を検索
            RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(() ->
                new RuntimeException("リフレッシュトークンが存在ございません。"));

            // DBに保存されている有効期限（LocalDateTime型）を取得
            LocalDateTime expirationLocalDateTime = refreshToken.getExpiryDate();

            // LocalDateTime → Date に変換（比較用）
            Date expirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

            // 現在時刻と比較して、まだ有効期限内であれば true を返す
            return expirationDate.after(new Date());

        } catch (Exception e) {
            // トークンが存在しない、または例外が発生した場合は無効とみなし false を返す
            return false;
        }
    }

}
