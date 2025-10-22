package dvn.local.dvnjs.modules.users.services.impl;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.entities.User;
import dvn.local.dvnjs.modules.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    // ユーザー情報を取得するためのリポジトリ（データベース操作用）
    private final UserRepository userRepository;

    /**
     * 認証時に呼び出されるメソッド。
     * 引数の userId（ここでは JWT の subject に格納された値）を使ってユーザー情報を検索する。
     * 
     * @param userId 認証対象ユーザーのID
     * @return Spring Security で使用される UserDetails オブジェクト
     * @throws UsernameNotFoundException ユーザーが存在しない場合にスローされる
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // データベースからユーザーを検索（存在しない場合は例外を投げる）
        User user = userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在ございません。"));
        
        // Spring Security の User オブジェクトを返す
        // 第1引数：ユーザー名（ここではメールアドレス）
        // 第2引数：パスワード（ハッシュ化済み）
        // 第3引数：権限リスト（ここでは空のリスト）
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.emptyList()
        );
    }
}
