package dvn.local.dvnjs.modules.users.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.entities.BlacklistedToken;
import dvn.local.dvnjs.modules.users.repositories.BlacklistedTokenRepository;
import dvn.local.dvnjs.modules.users.requests.BlacklistTokenRequest;
import dvn.local.dvnjs.resources.MessageResource;
import dvn.local.dvnjs.services.JwtService;

import io.jsonwebtoken.Claims;

/**
 * JWTトークンのブラックリスト管理を行うサービスクラス。
 * 
 * 主な役割：
 *  - 無効化されたトークン（例：ログアウト時）をデータベースに登録し、
 *    再利用を防止する。
 *  - トークンの有効期限やユーザーIDを保存する。
 */
@Service
public class BlackListService {

    // BlacklistedToken テーブルへアクセスするためのリポジトリ
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    // JWTトークンの解析・検証を行うサービス
    @Autowired
    private JwtService jwtService;

    // ログ出力用のロガー
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    /**
     * トークンをブラックリストに登録するメソッド。
     * 
     * @param request ブラックリスト登録リクエスト（トークン文字列を含む）
     * @return MessageResource 結果メッセージを返す
     */
    public Object create(BlacklistTokenRequest request) {
        try {
            // --- 1. トークンの重複チェック ---
            if (blacklistedTokenRepository.existsByToken(request.getToken())) {
                // 既に登録済みの場合は、処理を中断してメッセージを返す
                return new MessageResource("トークンは既に登録されています。");
            }
            logger.info("トークンの登録処理を開始します。");

            // --- 2. JWTトークンからクレーム情報を抽出 ---
            Claims claims = jwtService.getAllClaimsFromToken(request.getToken());

            // サブジェクト（ユーザーID）を取得
            Long userId = Long.valueOf(claims.getSubject());

            // トークンの有効期限を取得
            Date expiryDate = claims.getExpiration();

            // --- 3. エンティティ作成 ---
            BlacklistedToken blacklistedToken = new BlacklistedToken();

            // トークン文字列を設定
            blacklistedToken.setToken(request.getToken());

            // ユーザーIDを設定
            blacklistedToken.setUserId(userId);

            // Date → LocalDateTime に変換して有効期限を設定
            blacklistedToken.setExpiryDate(
                expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            );

            // --- 4. データベースに保存 ---
            blacklistedTokenRepository.save(blacklistedToken);

            logger.info("トークンをブラックリストに登録しました。");

            // --- 5. 成功メッセージを返す ---
            return new MessageResource("トークンは正常にブラックリストへ登録されました。");

        } catch (Exception e) {
            // --- 6. 予期しないエラーの処理 ---
            logger.error("ブラックリスト登録中にエラーが発生しました: {}", e.getMessage());
            return new MessageResource("ブラックリスト登録処理中にエラーが発生しました。 " + e.getMessage());
        }
    }
}
