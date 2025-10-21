package dvn.local.dvnjs.helpers;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

// import org.springframework.security.core.userdetails.UserDetailsService;

import dvn.local.dvnjs.databases.seeder.DatabaseSeeder;
import dvn.local.dvnjs.services.JwtService;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// JWT認証を行うためのフィルタークラス
// リクエストごとに一度だけ実行される（OncePerRequestFilterを継承）
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    // JWTを検証・生成するサービスクラス
    private final JwtService jwtService;

    // ユーザー情報を取得するためのサービス
    // private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    // フィルターのメイン処理
    @Override
    public void doFilterInternal(
        HttpServletRequest request,    // クライアントからのHTTPリクエスト
        HttpServletResponse response,  // サーバーからのHTTPレスポンス
        FilterChain filterChain        // 次のフィルターへの処理チェーン
    ) throws ServletException, IOException {

        // Authorizationヘッダーの取得（形式: "Bearer <JWT>" を想定）
        final String authHeader = request.getHeader("Authorization");

        // JWTトークンおよびメールアドレス（トークンから抽出）用の変数
        final String jwt;
        final String userId;

        // Authorizationヘッダーが無い、または "Bearer " で始まらない場合は401を返す
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // logger.error("test");
            // 後続フィルターへ処理を渡す
            filterChain.doFilter(request, response);
            return; // 処理をここで終了（次のフィルタへ進めない）
        }

        // "Bearer " の7文字をスキップして実トークン部分を取り出す
        jwt = authHeader.substring(7);

        // トークンからユーザー識別子（例：メールアドレス）を抽出
        userId = jwtService.getUserIdFromJwt(jwt); // ← 実装側のメソッド名に合わせてください

        logger.error(userId);
        // （この後の典型処理例）
        // 1) SecurityContextに認証情報が無ければ、トークン検証
        // 2) 有効ならUserDetailsを取得して認証コンテキストに登録
        // if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        //     UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        //     if (jwtService.isTokenValid(jwt, userDetails)) {
        //         UsernamePasswordAuthenticationToken authToken =
        //             new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        //         authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //         SecurityContextHolder.getContext().setAuthentication(authToken);
        //     }
        // }

        // 後続フィルターへ処理を渡す
        filterChain.doFilter(request, response);

    }
}
