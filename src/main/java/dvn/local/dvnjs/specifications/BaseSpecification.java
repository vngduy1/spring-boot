package dvn.local.dvnjs.specifications;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

/**
 * 共通で利用する Specification（検索条件）クラス
 * 動的クエリ生成用
 */
public class BaseSpecification<T> {

    /**
     * キーワード検索（like）用 Specification
     * 例: keyword=abc → name LIKE '%abc%' OR code LIKE '%abc%'
     *
     * @param keyword  検索キーワード
     * @param fields   対象カラム名（複数可）
     * @return         Specification<T>
     */
    public static <T> Specification<T> keyword(String keyword, String... fields) {
        return (root, query, criteriaBuilder) -> {

            // keyword が null または空の場合は条件なし
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction(); // WHERE 1 = 1 のイメージ
            }

            // OR 条件にするために Predicate 配列を作成
            Predicate[] predicates = new Predicate[fields.length];

            for (int i = 0; i < fields.length; i++) {
                // LOWER関数を使って大文字小文字を無視したLIKE検索
                predicates[i] = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(fields[i])),
                        "%" + keyword.toLowerCase() + "%"
                );
            }

            // OR 条件で返す
            return criteriaBuilder.or(predicates);
        };
    }

    /**
     * シンプルな WHERE 条件
     * 例: ?name=abc&status=1 → name = 'abc' AND status = '1'
     *
     * @param filters  Map<カラム名, 値>
     * @return         Specification<T>
     */
    public static <T> Specification<T> whereSpec(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {

            // forEach → equal 条件へ変換
            List<Predicate> predicates = filters.entrySet().stream()
                    .map(entry -> criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue()))
                    .collect(Collectors.toList());

            // AND 条件で返す
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    /**
     * 複雑な WHERE 条件 (比較・範囲・in)
     * 例:
     * /api/users?age[gte]=20&age[lte]=30&status[in]=ACTIVE,WAITING
     *
     * → age >= 20 AND age <= 30 AND status IN ('ACTIVE','WAITING')
     *
     * @param filters Map<String, Map<String, String>>
     *                key: フィールド名
     *                value: Map<演算子(eq, gte, lte, in), 値>
     * @return        Specification<T>
     */
    public static <T> Specification<T> complexWhereSpec(Map<String, Map<String, String>> filters) {
        return (root, query, criteriaBuilder) -> {

            // flatMap で多段Mapを Predicate に変換する
            List<Predicate> predicates = filters.entrySet().stream()
                    .flatMap(entry -> entry.getValue().entrySet().stream()
                            .map(condition -> {

                                String field = entry.getKey();     // カラム名
                                String operator = condition.getKey(); // 演算子 (eq, gt, lte etc...)
                                String value = condition.getValue();  // 値

                                // 演算子に応じて Predicate を作成
                                switch (operator.toLowerCase()) {

                                    case "eq" -> {
                                        return criteriaBuilder.equal(root.get(field), value);
                                    }
                                    case "gt" -> {
                                        return criteriaBuilder.greaterThan(root.get(field), value);
                                    }
                                    case "gte" -> {
                                        return criteriaBuilder.greaterThanOrEqualTo(root.get(field), value);
                                    }
                                    case "lt" -> {
                                        return criteriaBuilder.lessThan(root.get(field), value);
                                    }
                                    case "lte" -> {
                                        return criteriaBuilder.lessThanOrEqualTo(root.get(field), value);
                                    }
                                    case "in" -> {
                                        // カンマ区切りを List に変換
                                        List<String> values = List.of(value.split(","));
                                        return root.get(field).in(values);
                                    }

                                    default -> throw new AssertionError(
                                            "演算子「" + operator + "」は未対応です。実装してください。"
                                    );
                                }
                            })
                    ).collect(Collectors.toList());

            // AND 条件で返す
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
