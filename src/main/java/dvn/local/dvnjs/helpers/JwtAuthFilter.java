package dvn.local.dvnjs.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.services.impl.CustomUserDetailsService;
import dvn.local.dvnjs.services.JwtService;



/**
 * 🔐【クラス概要】
 * JwtAuthFilter クラスは、HTTPリクエストに含まれる JWT（JSON Web Token）を検証し、
 * 正しいトークンである場合にユーザー認証を行うフィルターです。
 *
 * 主な役割：
 *  - 各リクエストごとに一度だけ実行（OncePerRequestFilter 継承）
 *  - Authorization ヘッダーから "Bearer <token>" を取得
 *  - トークンのフォーマットと有効性を確認
 *  - JWT から userId を抽出し、Spring Security のコンテキストに認証情報を設定
 *  - エラー発生時は JSON 形式でエラーレスポンスを返す
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    // JWTの生成・検証を行うサービス
    private final JwtService jwtService;

    // ユーザー情報を取得するサービス
    private final CustomUserDetailsService customUserDetailsService;

    // JSON出力用（エラーレスポンスなど）
    private final ObjectMapper objectMapper;

    // ロガーの設定
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);


    /**
     * 【メソッド概要】
     * 特定のURL（例：ログインAPI）はフィルタを適用しないように除外する。
     * 
     * @param request 現在のHTTPリクエスト
     * @return true の場合、このフィルタをスキップ
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // /api/v1/auth/login のリクエストはフィルタ対象外にする
        return path.startsWith("/api/v1/auth/login");
    }


    /**
     * 【メソッド概要】
     * JWT認証のメイン処理を行う。
     * 1. AuthorizationヘッダーからJWTを取得
     * 2. トークン形式と内容を検証
     * 3. ユーザー情報を読み込み、認証コンテキストを設定
     * 4. エラー発生時はJSON形式でレスポンスを返す
     */
    @Override
    public void doFilterInternal(
        @Nonnull HttpServletRequest request,    // クライアントからのリクエスト
        @Nonnull HttpServletResponse response,  // サーバーからのレスポンス
        @Nonnull FilterChain filterChain        // 次のフィルターへのチェーン
    ) throws ServletException, IOException {

        try {
            // Authorizationヘッダーの取得（形式: "Bearer <JWT>"）
            final String authHeader = request.getHeader("Authorization");

            final String jwt;
            final String userId;

            // ヘッダーが存在しない、またはBearerトークンでない場合はエラー返却
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response,
                        request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "認証できませんでした。",
                        "トークンが見つかりません。");
                return;
            }

            // "Bearer " の7文字をスキップしてトークン本体を抽出
            jwt = authHeader.substring(7);

            // トークンの形式をチェック
            if (!jwtService.isTokenFormatValid(jwt)) {
                sendErrorResponse(response,
                        request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。",
                        "トークンの定義は正しくありません。");
                return;
            }


            // JWTトークンからユーザーIDを取得
            userId = jwtService.getUserIdFromJwt(jwt);

            // 署名検証：失敗なら401
            if (!jwtService.isSignatureValid(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。", "トークンの署名が不正です。");
                return;
            }
            // 発行者チェック：不一致なら401
            if (!jwtService.isIssuerToken(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。", "トークンの発行者が不正です。");
                return;
            }

            // 期限切れチェック：期限切れなら401
            if (jwtService.isTokenExpired(jwt)) {

                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。", "トークンの有効期限が切れています。");
                return;
            }

            //トークンフロックなら
            if (jwtService.isBlackListedToken(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。", "トークンはブロックされました。");
                return;
            }
            
            // SecurityContext に認証情報が設定されていない場合
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                final String emailFromToken = jwtService.getEmailFromJwt(jwt);

                if (!emailFromToken.equals(userDetails.getUsername())) {
                    sendErrorResponse(response,
                        request, HttpServletResponse.SC_UNAUTHORIZED,
                        "認証できませんでした。",
                        "ユーザートークンが正くありません。");
                    return;
                }
                // 認証トークンの作成（必要に応じて有効化）
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

            // 次のフィルターへ処理を渡す
            filterChain.doFilter(request, response);

        } catch (ServletException | IOException e) {
            // 想定外のエラー発生時の処理
            sendErrorResponse(response,
                    request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "認証できませんでした。",
                    "インターネットのエラー発生しました。");
        }
    }


    /**
     * 【メソッド概要】
     * エラー発生時にJSON形式でレスポンスを返す共通メソッド。
     *
     * @param response HTTPレスポンス
     * @param request HTTPリクエスト
     * @param statusCode ステータスコード
     * @param error エラー概要
     * @param message 詳細メッセージ
     */
    private void sendErrorResponse(
        HttpServletResponse response,
        HttpServletRequest request,
        int statusCode,
        String error,
        String message
    ) throws IOException {

        // ステータスコード、エンコーディング、ContentTypeの設定
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        // エラー情報をマップに格納
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        // MapをJSONに変換して出力
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
