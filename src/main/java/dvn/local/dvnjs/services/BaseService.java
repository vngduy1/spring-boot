package dvn.local.dvnjs.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service // 共通のサービスクラスとしてSpringに登録
public class BaseService {

    /**
     * ソート条件を生成するメソッド
     * 
     * @param sortParam 例: "name,asc" または "createdAt,desc"
     * @return Sort オブジェクト（Spring Data用）
     * 
     * 処理の流れ:
     * ① sortParam が null または 空 の場合 → id 降順でソート
     * ② sortParam を「,」で分割し、フィールド名とソート方向を取得
     * ③ asc / desc に応じて Sort オブジェクトを返す
     */
    protected Sort createSort(String sortParam) {
        // ソートパラメータが指定されていない場合、id降順
        if (sortParam == null || sortParam.isEmpty()) {
            return Sort.by(Sort.Order.desc("id"));
        }

        // 例: "name,asc" → ["name", "asc"]
        String[] parts = sortParam.split(",");
        String field = parts[0];
        String sortDirection = (parts.length > 1) ? parts[1] : "asc";

        // 昇順 / 降順を判定してSortオブジェクトを生成
        if ("desc".equalsIgnoreCase(sortDirection)) {
            return Sort.by(Sort.Order.desc(field));
        } else {
            return Sort.by(Sort.Order.asc(field));
        }
    }
}
