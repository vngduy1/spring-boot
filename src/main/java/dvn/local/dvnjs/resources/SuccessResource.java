package dvn.local.dvnjs.resources;


// 成功時のレスポンスデータを格納するクラス
public class SuccessResource<T> {

    // メッセージを保持するフィールド
    private String message;

    // 汎用データを保持するフィールド（ジェネリック型T）
    private T data;

    // コンストラクタ：メッセージとデータを受け取り初期化する
    public SuccessResource(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // メッセージを取得するメソッド
    public String getMessage() {
        return message;
    }

    // メッセージを設定するメソッド
    public void setMessage(String message) {
        this.message = message;
    }

    // データを取得するメソッド  
    public T getData() {
        return data;
    }

    // データを設定するメソッド  
    public void setData(T data) {
        this.data = data;
    }

}

