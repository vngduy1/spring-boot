package dvn.local.dvnjs.modules.users.resources;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

// ユーザー情報を表すリソースクラス
// （クライアントへ返すユーザーデータのフォーマットを定義）
@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCatalogueResource {
    
    // ユーザーID
    private final Long id;

    // メールアドレス
    private final String name;

    // ユーザー名
    private final Integer publish;
}
