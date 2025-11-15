package dvn.local.dvnjs.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvc;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.services.interfaces.BaseServiceInterface;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected S service;

    @MockBean
    protected M mapper;

    // @MockBean
    // protected EntityManagerFactory entityManagerFactory;

    // @MockBean
    // protected EntityManager entityManager;

    protected abstract String getApiPath();
    protected abstract List<E> createTestEntities();

    protected abstract List<R> createTestResources();
    
    @Test
    void list_NoFilter_shouldReturnAllRecords() throws Exception {
        List<E> mockEntities = createTestEntities();
        List<R> mockResources = createTestResources();

        ArgumentCaptor<Map<String, String[]>> captor = ArgumentCaptor.forClass(Map.class);

        when(service.getAll(captor.capture())).thenReturn(mockEntities);
        when(mapper.toList(mockEntities)).thenReturn(mockResources);

        mockMvc.perform(get(getApiPath() + "/list").contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist());
            
        verify(service).getAll(captor.getValue());
        verify(mapper).toList(mockEntities);
    }
}
