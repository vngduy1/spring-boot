package dvn.local.dvnjs.databases.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component // Spring Boot の起動時に自動実行されるクラス
public class DatabaseSeeder implements CommandLineRunner {

    @PersistenceContext // EntityManager（JPA操作用オブジェクト）の注入
    private EntityManager entityManager;

    @Autowired // パスワード暗号化クラスの自動注入
    private PasswordEncoder passwordEncoder;
    
    @Transactional  // DBトランザクション内で実行（途中でエラーが発生した場合はロールバック）   
    @Override
    public void run(String... args) throws Exception {
        // テーブルが空の場合のみ実行
        if (isTableEmpty()) {

            // パスワードを暗号化
            String passwordEncode = passwordEncoder.encode("password");
            // SQLを使用して初期データを登録
            entityManager.createNativeQuery(
                "INSERT INTO users (name, email, password, user_catalogue_id, phone) " +
                "VALUES (?, ?, ?, ?, ?)"
            )
            // 各パラメータを設定（? の順番に対応）
            .setParameter(1, "Nam Hoàng Văn")              // ユーザー名
            .setParameter(2, "chandanvoi010@gmail.com")    // メールアドレス
            .setParameter(3, passwordEncode)               // 暗号化済みパスワード
            .setParameter(4, 1L)                           // ユーザーカタログID
            .setParameter(5, "0982365824")                 // 電話番号
        .executeUpdate(); // SQL実行（データ登録）

            // コンソール出力（デバッグ用）
            System.out.println("password = " + passwordEncode);

            // ★ここでデータ登録を行う場合、EntityManager.persist() または Repository.save() を使用します。
            // 例:
            // User user = new User();
            // user.setName("Admin");
            // user.setEmail("admin@example.com");
            // user.setPassword(passwordEncode);
            // entityManager.persist(user);
        }
    }

    // テーブルが空かどうかを判定するメソッド
    private boolean isTableEmpty() {
        // COUNTクエリで件数を取得（Userエンティティを対象）
        Long count = (Long) entityManager.createQuery("SELECT COUNT(u.id) FROM User u").getSingleResult();
        return count == 0;
    }
}
