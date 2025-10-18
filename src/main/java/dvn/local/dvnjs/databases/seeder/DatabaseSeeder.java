package dvn.local.dvnjs.databases.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dvn.local.dvnjs.modules.users.entities.User;
import dvn.local.dvnjs.modules.users.repositories.UserRepository;

@Component // Spring Boot の起動時に自動実行されるクラス
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @PersistenceContext // EntityManager（JPA操作用オブジェクト）の注入
    private EntityManager entityManager;

    @Autowired // パスワード暗号化クラスの自動注入
    private PasswordEncoder passwordEncoder;

    // Spring によって UserRepository のインスタンスが自動的に注入される
    // （手動で new UserRepository() を作成する必要がない）
    @Autowired
    private UserRepository userRepository;
    
    @Transactional  // DBトランザクション内で実行（途中でエラーが発生した場合はロールバック）   
    @Override
    public void run(String... args) throws Exception {

        logger.info("seeder running 1234:...");

        // logger.info("running");
        // テーブルが空の場合のみ実行
        if (isTableEmpty()) {

            // パスワードを暗号化
            String passwordEncode = passwordEncoder.encode("password");

            // ▼ 参考：Native SQL で入れる場合の雛形（今回は Repository.save を使用）
            // entityManager.createNativeQuery(
            //     "INSERT INTO users (name, email, password, user_catalogue_id, phone) " +
            //     "VALUES (?, ?, ?, ?, ?)"
            // );
            // 各パラメータを設定（? の順番に対応）
            // .setParameter(1, "Nam Hoàng Văn")              // ユーザー名
            // .setParameter(2, "chandanvoi010@gmail.com")    // メールアドレス
            // .setParameter(3, passwordEncode)               // 暗号化済みパスワード
            // .setParameter(4, 1L)                           // ユーザーカタログID
            // .setParameter(5, "0982365824")                 // 電話番号
            // .executeUpdate(); // SQL実行（データ登録）

            // コンソール出力（デバッグ用）
            //System.out.println("password = " + passwordEncode);

            // ★ 推奨：エンティティを使って登録（監査項目や自動カラムに対応しやすい）
            // User user = new User();
            // user.setName("Nam Hoàng Văn");          // ユーザー名
            // user.setEmail("admin@example.com");     // メール（ユニーク制約がある場合は重複に注意）
            // user.setPassword(passwordEncode);       // 暗号化済みパスワード
            // user.setUserCatalogueId(1L);            // 関連 ID（FK など）
            // user.setPhone("0982365824");            // 電話番号

            User user = new User();
            user.setName("Admin"); // ユーザー名
            user.setEmail("admin@example.com"); // メール（ユニーク制約がある場合は重複に注意）
            user.setPassword(passwordEncode); // 暗号化済みパスワード
            user.setUserCatalogueId(1L); // 関連 ID（FK など）
            user.setPhone("0982365824"); // 電話番号

            // ※ ここで 'update_at' / 'created_at' などのカラムが DB に存在しないと
            //    Hibernate が INSERT 時にエラーになります。スキーマとエンティティのアノテーション
            //    （@Column(name="update_at") など）を一致させてください。
            userRepository.save(user); // 1件登録（永続化）
            logger.info("DatabaseSeeder: 初期ユーザーを登録しました。");
        } else {
            logger.info("DatabaseSeeder: users テーブルにデータが存在するため、初期データの登録をスキップします。");  // Bỏ qua vì bảng không trống
        }
    }

    // テーブルが空かどうかを判定するメソッド
    private boolean isTableEmpty() {
        // COUNTクエリで件数を取得（Userエンティティを対象）
        Long count = (Long) entityManager.createQuery("SELECT COUNT(u.id) FROM User u").getSingleResult();
        return count == 0;
    }
}
