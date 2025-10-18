package dvn.local.dvnjs.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.HashMap;

import dvn.local.dvnjs.resources.ErrorResource;

@ControllerAdvice // 全てのコントローラーで発生する例外を共通的に処理するクラスであることを示す
public class GlobalExceptionHandler {

    /**
     * このクラスは、@Valid アノテーションによる入力チェック（バリデーション）で
     * エラーが発生した場合に例外をキャッチして処理するための共通ハンドラーです。
     * 
     * MethodArgumentNotValidException を検知し、
     * エラーとなったフィールド名とメッセージをマップにまとめて
     * HTTPステータス400（Bad Request）で返します。
     * 
     * これにより、入力エラーが発生した際に統一されたJSON形式の
     * エラーレスポンスをクライアントに返すことができます。
     */
    // バリデーションエラー（@Valid などで発生する）をキャッチする
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException(MethodArgumentNotValidException exception) {

        // エラーメッセージを格納するためのマップ（キー＝フィールド名、値＝エラーメッセージ）
        Map<String, String> errors = new HashMap<>();

        // バリデーションエラーの全リストを取得し、1件ずつ処理
        exception.getBindingResult().getAllErrors().forEach(error -> {
            // どのフィールドでエラーが発生したかを取得
            String fieldName = ((FieldError) error).getField();
            // そのフィールドのエラーメッセージを取得
            String errorMessage = error.getDefaultMessage();
            // マップに登録（例：「email」→「メールアドレスの形式が正しくありません」）
            errors.put(fieldName, errorMessage);
        });

        // エラーメッセージと詳細情報をErrorResourceに格納し、ログにも出力する
        ErrorResource errorResource = new ErrorResource("データベースを確認するとき、エラー発生しました。", errors);

        // マップ（errors）をレスポンスボディとして返す（HTTPステータスは400 Bad Request）
        return new ResponseEntity<>(errorResource, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

