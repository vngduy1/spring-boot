package dvn.local.dvnjs.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * メッセージレスポンスを表すクラス。
 * 
 * API のレスポンスとしてクライアントへ返すメッセージを保持するために使用される。
 * 例：処理成功メッセージ、エラーメッセージなど。
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageResource {

    /**
     * クライアントに返すメッセージ内容。
     * 例：「トークンは正常に登録されました。」など
     */
    private String message;
}
