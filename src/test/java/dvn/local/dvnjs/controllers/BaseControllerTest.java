package dvn.local.dvnjs.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.test.web.servlet.MockMvc;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.services.interfaces.BaseServiceInterface;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.web.servlet.ResultActions;

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
    protected MockMvc mockMvc;  // MockMvcインスタンス

    @MockBean
    protected S service;   // サービスのモック

    @MockBean
    protected M mapper;    // マッパーのモック

    // APIパスを取得するための抽象メソッド
    protected abstract String getApiPath();

    protected abstract String getTestKeyword();
    protected abstract Map<String, String[]> getTestSimpleFilter();
    protected abstract ResultActions getExpectResponseData(ResultActions result, List<R> resource) throws Exception;

    // テスト用のエンティティとリソースを作成するための抽象メソッド
    protected abstract List<E> createTestEntities();
    protected abstract List<E> createTestEntitiesByKeywordFilter(List<E> entities, String keyword);
    protected abstract List<E> createTestEntitiesBySimpleFiltered(List<E> entity, Map<String, String[]> simpleFilter);
    
    // リソース用のフィルタリングメソッド
    protected abstract List<R> createTestResources();
    protected abstract List<R> createTestResourcesByKeywordFilter(List<R> resources, String keyword);
    protected abstract List<R> createTestResourceBySimpleFiltered(List<R> resources);
     
    // リスト取得のテストケース
    @Test
    void list_NoFilter_shouldReturnAllRecords() throws Exception {
        List<E> mockEntities = createTestEntities();
        List<R> mockResources = createTestResources();

        @SuppressWarnings("unchecked")
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
    
    // リスト取得のフィルタリングテストケース（キーワードフィルター）
    @Test
    void list_withKeywordFilter_shouldReturnFilteredKeywordRecords() throws Exception {
        List<E> mockEntities = createTestEntities();
        List<R> mockResources = createTestResources();
        List<E> mockFilterEntities = createTestEntitiesByKeywordFilter(mockEntities, getTestKeyword());
        List<R> mockFilterResources = createTestResourcesByKeywordFilter(mockResources, getTestKeyword());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String[]>> captor = ArgumentCaptor.forClass(Map.class);

        when(service.getAll(captor.capture())).thenReturn(mockFilterEntities);
        when(mapper.toList(mockFilterEntities)).thenReturn(mockFilterResources);

        ResultActions actions = mockMvc.perform(get(getApiPath() + "/list")
            .param("keyword", getTestKeyword())
                .contentType("application/json"));
        
        getExpectResponseData(actions, mockFilterResources)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist());

        Map<String, String[]> capturedParams = captor.getValue();
        assertThat(capturedParams.get("keyword")).containsExactly(getTestKeyword());

        verify(service).getAll(captor.getValue());
        verify(mapper).toList(mockFilterEntities);
    }
    
    // @Test
    // void list_withSimpleFilter_ShouldReturnSimpleFilteredRecords() throws Exception {
    //     String publishValue = "2";
    //     Map<String, String[]> simpleFilter = getTestSimpleFilter();
    //     List<E> mockEntities = createTestEntities();
    //     List<R> mockResources = createTestResources();
    //     List<E> mockFilterEntities = createTestEntitiesBySimpleFiltered(mockEntities, simpleFilter);
    //     List<R> mockFilterResources = createTestResourceBySimpleFiltered(mockResources);

    //     @SuppressWarnings("unchecked")
    //     ArgumentCaptor<Map<String, String[]>> captor = ArgumentCaptor.forClass(Map.class);

    //     ResultActions actions = mockMvc.perform(get(getApiPath() + "/list")
    //         .param("publish", getTestKeyword())
    //             .contentType("application/json"));

    //     when(service.getAll(captor.capture())).thenReturn(mockFilterEntities);
    //     when(mapper.toList(mockFilterEntities)).thenReturn(mockFilterResources);

    //     getExpectResponseData(actions, mockFilterResources)
    //         .andDo(print())
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$.success").value(true))
    //         .andExpect(jsonPath("$.message").value("SUCCESS"))
    //         .andExpect(jsonPath("$.status").value("OK"))
    //         .andExpect(jsonPath("$.data").isArray())
    //         .andExpect(jsonPath("$.timestamp").exists())
    //         .andExpect(jsonPath("$.errors").doesNotExist())
    //         .andExpect(jsonPath("$.error").doesNotExist());

    //     Map<String, String[]> capturedParams = captor.getValue();
    //     assertThat(capturedParams.get("publish")).containsExactly(publishValue);

    //     verify(service).getAll(captor.getValue());
    //     verify(mapper).toList(mockFilterEntities);
    // }   
}
