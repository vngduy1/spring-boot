package dvn.local.dvnjs.controllers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.services.interfaces.BaseServiceInterface;

/*
 * E: エンティティの型
 * R: リソースの型
 * C: 作成リクエストの型
 * U: 更新リクエストの型
 * Repo: リポジトリの型（JpaRepositoryとJpaSpecificationExecutorを継承）
 * M: マッパーの型
 * S: サービスインターフェースの型
 */
public abstract class BaseControllerTest <
    E,
    R,
    C,
    U,
    Repo extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>,
    M extends BaseMapper<E, R, C, U>,
    S extends BaseServiceInterface<E, C, U>
> {

    @MockBean

    protected abstract String getApiPath();
    protected abstract List<E> createTestEntities();
    protected abstract List<R> createTestResources();
}
