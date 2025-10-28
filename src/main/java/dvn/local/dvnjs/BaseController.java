package dvn.local.dvnjs;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * BaseController
 * 
 * <p>
 * データベース接続テスト用のコントローラー。  
 * /v1/api/test エンドポイントにアクセスすると、  
 * 指定したテーブル（test_table）が存在しない場合は自動的に作成される。
 * </p>
 */
@RestController
@RequestMapping("v1/api")
public class BaseController {

    // Springが管理するJDBCテンプレート（SQL実行用）
    private final JdbcTemplate jdbcTemplate;

    /**
     * コンストラクタインジェクションによりJdbcTemplateを受け取る。
     * 
     * @param jdbcTemplate SpringのJDBCテンプレートオブジェクト
     */
    public BaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * データベース接続テスト用エンドポイント。
     * 
     * <p>
     * アクセス時に「test_table」テーブルが存在しなければ新規作成する。  
     * 成功時には "successfully" を返却。
     * </p>
     * 
     * @return 実行結果のメッセージ文字列
     */
    @GetMapping("test")
    public String test() {

        // テーブルが存在しない場合のみ作成するSQL文
        String sql = "CREATE TABLE IF NOT EXISTS test_table ("
           + "id INT AUTO_INCREMENT PRIMARY KEY, "
           + "name VARCHAR(255) NOT NULL"
           + ")";

        // SQLを実行
        jdbcTemplate.execute(sql);

        // 成功メッセージを返却
        return "successfully";
    }
}
