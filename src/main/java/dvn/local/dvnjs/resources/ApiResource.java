package dvn.local.dvnjs.resources;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

// APIレスポンスの共通クラス（汎用的なレスポンス形式を定義）
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResource<T> {

    // 成功したかどうか（true: 成功, false: 失敗）
    private boolean success;

    // メッセージ（成功・エラーメッセージなど）
    private String message;

    // データ本体（成功時の返却データ）
    private T data;

    // HTTPステータスコード（例：200, 400など）
    private HttpStatus status;

    // タイムスタンプ（レスポンス生成時刻）
    private LocalDateTime timestamp;

    // エラー情報（失敗時のみ）
    private ErrorResource error;

    // --- エラー情報をまとめた内部クラス ---
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResource {
        // エラーコード（任意）
        private String code;

        // エラーメッセージ
        private String message;

        // 詳細情報（例：スタックトレースや補足説明など）
        private String detail;

        // メッセージのみのコンストラクタ
        public ErrorResource(String message) {
            this.message = message;
        }

        // コード＋メッセージのコンストラクタ
        public ErrorResource(String code, String message) {
            this.code = code;
            this.message = message;
        }

        // コード＋メッセージ＋詳細のコンストラクタ
        public ErrorResource(String code, String message, String detail) {
            this.code = code;
            this.message = message;
            this.detail = detail;
        }
    }

    // コンストラクタ（作成時に現在時刻をセット）
    private ApiResource() {
        this.timestamp = LocalDateTime.now();
    }

    // Builderパターンで生成するためのメソッド
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    // --- Builderクラス ---
    public static class Builder<T> {
        private final ApiResource<T> resource;

        private Builder() {
            resource = new ApiResource<>();
        }

        // 成功・失敗フラグを設定
        public Builder<T> success(boolean success) {
            resource.success = success;
            return this;
        }

        // メッセージを設定
        public Builder<T> message(String message) {
            resource.message = message;
            return this;
        }

        // データを設定
        public Builder<T> date(T data) {  // ← ここは "data" のタイポかもしれません
            resource.data = data;
            return this;
        }

        // ステータスを設定
        public Builder<T> status(HttpStatus status) {
            resource.status = status;
            return this;
        }

        // エラー情報を設定
        public Builder<T> error(ErrorResource error) {
            resource.error = error;
            return this;
        }

        // 最後にオブジェクトを作成して返す
        public ApiResource<T> build() {
            return resource;
        }
    }
    
    // --- 成功時のレスポンスを作成する便利メソッド ---
    public static <T> ApiResource<T> ok(T data, String message) {
        return ApiResource.<T>builder()
                .success(true)
                .date(data)
                .message(message)
                .status(HttpStatus.OK)
                .build();
    }

    // メッセージのみを返す成功レスポンス
    public static <T> ApiResource<T> message(String message, HttpStatus status) {
        return ApiResource.<T>builder()
                .success(true)
                .message(message)
                .status(status)
                .build();
    }

    // エラー時のレスポンスを作成
    public static <T> ApiResource<T> error(String code, String message, HttpStatus status) {
        return ApiResource.<T>builder()
                .success(false)
                .error(new ErrorResource(code, message))
                .status(status)
                .build();
    }
}
