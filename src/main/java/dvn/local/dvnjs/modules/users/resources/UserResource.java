package dvn.local.dvnjs.modules.users.resources;

// ユーザー情報を表すリソースクラス
// （クライアントへ返すユーザーデータのフォーマットを定義）
public class UserResource {

    // ユーザーID
    private final Long id;

    // メールアドレス
    private final String email;

    // ユーザー名
    private final String name;

    // コンストラクタ（IDとメールアドレスを受け取る）
    // ※注意：nameが引数に含まれていないため、常にnullになります。
    public UserResource(
        Long id,
        String email,
        String name
    ) {
        this.id = id;
        this.email = email;
        this.name = name; 
    }

    // ユーザーIDを返す
    public Long getId() {
        return id;
    }

    // メールアドレスを返す
    public String getEmail() {
        return email;
    }

    // ユーザー名を返す
    public String getName() {
        return name;
    }
}
