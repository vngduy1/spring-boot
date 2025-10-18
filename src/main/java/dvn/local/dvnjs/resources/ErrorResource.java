package dvn.local.dvnjs.resources;

import java.util.Map;

/**
 * エラー情報を保持するためのリソースクラス。
 * 
 * 主にAPIのエラーレスポンスで使用され、
 * 全体のメッセージ（例：「入力値が不正です」）と、
 * 各フィールドごとの詳細なエラーメッセージを格納します。
 */
public class ErrorResource {

    // 全体的なエラーメッセージ（例：「バリデーションエラーが発生しました」など）
    private String message;

    // 各フィールドごとのエラー内容（キー：フィールド名、値：メッセージ）
    private Map<String, String> errors;

    // コンストラクタ（メッセージとエラー情報を初期化）
    public ErrorResource(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }

    // 全体メッセージを取得
    public String getMessage() {
        return message;
    }

    // 全体メッセージを設定
    public void setMessage(String message) {
        this.message = message;
    }

    // エラーマップを取得
    public Map<String, String> getErrors() {
        return errors;
    }

    // エラーマップを設定
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}

