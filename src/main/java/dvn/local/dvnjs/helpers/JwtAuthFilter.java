package dvn.local.dvnjs.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.modules.users.services.impl.CustomUserDetailsService;
import dvn.local.dvnjs.services.JwtService;
import jakarta.validation.constraints.NotNull;


// JWT認証を行うためのフィルタークラス
// リクエストごとに一度だけ実行される（OncePerRequestFilterを継承）
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    // JWTを検証・生成するサービスクラス
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    // ユーザー情報を取得するためのサービス
    // private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    // フィルターのメイン処理
    @Override
    public void doFilterInternal(
        @Nonnull HttpServletRequest request,    // クライアントからのHTTPリクエスト
        @Nonnull HttpServletResponse response,  // サーバーからのHTTPレスポンス
        @Nonnull FilterChain filterChain        // 次のフィルターへの処理チェーン
    ) throws ServletException, IOException {

        // Authorizationヘッダーの取得（形式: "Bearer <JWT>" を想定）
        final String authHeader = request.getHeader("Authorization");

        // JWTトークンおよびメールアドレス（トークンから抽出）用の変数
        final String jwt;
        final String userId;

        // Authorizationヘッダーが無い、または "Bearer " で始まらない場合は401を返す
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED, "認証できませんでした。", "トークンは見つかりません。");
            // 後続フィルターへ処理を渡す
            // filterChain.doFilter(request, response);
            return; // 処理をここで終了（次のフィルタへ進めない）
        }

        // "Bearer " の7文字をスキップして実トークン部分を取り出す
        jwt = authHeader.substring(7);

        // トークンからユーザー識別子（例：メールアドレス）を抽出
        userId = jwtService.getUserIdFromJwt(jwt); // ← 実装側のメソッド名に合わせてください　

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.info("確認成功" + userDetails.getUsername());
        }

        // 後続フィルターへ処理を渡す
        filterChain.doFilter(request, response);

    }
    
    private void sendErrorResponse(
         HttpServletResponse response,
         HttpServletRequest request,
        int statusCode,
        String error,
        String message
    )throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);

    }

}
