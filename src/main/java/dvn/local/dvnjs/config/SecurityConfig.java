package dvn.local.dvnjs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import dvn.local.dvnjs.helpers.JwtAuthFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtAuthFilter jwtAuthFilter;

    // セキュリティ設定を定義するメソッド
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF保護を無効化（API通信のため）
            .csrf(csrf -> csrf.disable())

            // HTTPリクエストの認可設定
            .authorizeHttpRequests(auth -> auth

                // 特定のURLパスを認証なしでアクセス許可
                .requestMatchers(
                    "/api/v1/auth/login"            // ログイン用API
                        ).permitAll() // 上記のAPIは全てのユーザーにアクセスを許可
                
                // ② 公開ルート（認証不要のAPI）設定
                .requestMatchers(
                    "/api/v1/products"   // 商品情報取得用API（誰でもアクセス可能）
                ).permitAll()

                // ③ その他の全てのリクエストは認証が必要
                .anyRequest().authenticated()
            )

            // ④ セッション管理の設定
            .sessionManagement(session -> session
                // サーバー側でセッションを保持しない（JWT認証のため）
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )    

            // ⑤ JWTフィルターをSpring Securityの認証フィルターの前に追加
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    } 
}
