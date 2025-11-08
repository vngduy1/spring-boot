package dvn.local.dvnjs.modules.users.services.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface BaseServiceInterface<E, C, U> {
    // CRUD操作のインターフェース定義
    E create(C request);
    // updateメソッドのシグネチャ
    E update(Long id, U request);
    // deleteメソッドのシグネチャ
    Boolean delete(Long id);
    Boolean deleteMultipleEntity(List<Long> id);
    Page<E> paginate(Map<String, String[]> parameters);
    List<E> getAll(Map<String, String[]> parameters);
}
