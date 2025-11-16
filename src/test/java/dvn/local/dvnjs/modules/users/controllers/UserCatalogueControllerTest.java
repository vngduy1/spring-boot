package dvn.local.dvnjs.modules.users.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.ResultActions;

import dvn.local.dvnjs.config.SecurityConfig;
import dvn.local.dvnjs.controllers.BaseControllerTest;
import dvn.local.dvnjs.helpers.JwtAuthFilter;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.mappers.UserCatalogueMapper;
import dvn.local.dvnjs.modules.users.repositories.UserCatalogueRepository;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;

@WebMvcTest(
    value = UserCatalogueController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {JwtAuthFilter.class, SecurityConfig.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
public class UserCatalogueControllerTest extends BaseControllerTest<
    UserCatalogue,
    UserCatalogueResource,
    StoreRequest,
    UpdateRequest,
    UserCatalogueRepository,
    UserCatalogueMapper,
    UserCatalogueServiceInterface
>
{
    
    @Override
    protected String getApiPath() {
        return "/api/v1/user_catalogues";
    }

    @Override
    protected String getTestKeyword() {
        return "Admin";
    }

    @Override
    protected List<UserCatalogue> createTestEntities() {

        return Arrays.asList(
            UserCatalogue.builder()
                .id(1L)
                .name("nhóm Admin")
                .publish(1)
                .createdAt(LocalDateTime.now())
                .build(),
            UserCatalogue.builder()
                .id(2L)
                .name("Nhóm Seller")
                .publish(1)
                .createdAt(LocalDateTime.now())
                .build(),
            UserCatalogue.builder()
                .id(3L)
                .name("Nhóm Cộng tác vien")
                .publish(2)
                .createdAt(LocalDateTime.now())
                .build(),
            UserCatalogue.builder()
            .id(4L)
            .name("Nhóm ho tro")
            .publish(2)
            .createdAt(LocalDateTime.now())
            .build()
        );
    

    }

    @Override
    protected List<UserCatalogueResource> createTestResources() {
        return Arrays.asList(
                UserCatalogueResource.builder()
                        .id(1L)
                        .name("nhóm Admin")
                        .publish(1)
                        .build(),
                UserCatalogueResource.builder()
                        .id(2L)
                        .name("Nhóm Seller")
                        .publish(2)
                        .build(),
                UserCatalogueResource.builder()
                        .id(3L)
                        .name("Nhóm Cộng tác vien")
                        .publish(2)
                        .build(),
                UserCatalogueResource.builder()
                        .id(4L)
                        .name("Nhóm ho tro")
                        .publish(2)
                        .build()
            );
                            
    }
    
    @Override
    protected List<UserCatalogue> createTestEntitiesByKeywordFilter(List<UserCatalogue> entities, String keyword) {
        return entities.stream()
                .filter(e -> e.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    protected List<UserCatalogueResource> createTestResourcesByKeywordFilter(List<UserCatalogueResource> resources,
            String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        return resources.stream()
                .filter(r -> r.getName().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    protected List<UserCatalogue> createTestEntitiesBySimpleFiltered(List<UserCatalogue> entities, Map<String, String[]> filters) {
        return entities.stream()
            .filter(entry -> filters.entrySet().stream().allMatch(param -> {
                try {
                    String key = param.getKey();  // フィルタリングキー
                    String[] values = param.getValue(); // フィルタリング値の配列
                    // フィルタリングキーに対応するゲッターメソッド名を生成
                    String getterMethod = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                    Method getter = entry.getClass().getMethod(getterMethod);
                    Object fieldValue = getter.invoke(entry);
                
                    if (fieldValue == null) {
                        return true;
                    }
                    return Arrays.stream(values).map(value -> fieldValue instanceof Integer ? Integer.valueOf(value) : value)
                            .allMatch(value -> value.equals(fieldValue));

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    return true;
                }
            }))
            .collect(Collectors.toList());
    }

    
    @Override
    protected List<UserCatalogueResource> createTestResourceBySimpleFiltered(List<UserCatalogueResource> resources) {
        return resources.stream()
                .filter(e -> e.getPublish() == 2)
                .collect(Collectors.toList());
    }

    @Override
    protected ResultActions getExpectResponseData(ResultActions result, List<UserCatalogueResource> resource) throws Exception {
        
        result.andExpect(jsonPath("$.data", hasSize(resource.size())));

        for(int i = 0; i < resource.size(); i++) {
            UserCatalogueResource res = resource.get(i);
            result.andExpect(jsonPath("$.data[" + i + "].id").value(res.getId()))
                  .andExpect(jsonPath("$.data[" + i + "].name", containsString(getTestKeyword())))
                  .andExpect(jsonPath("$.data[" + i + "].publish").value(res.getPublish()));
        }

        return result;
    }
    
    @Override
    protected Map<String, String[]> getTestSimpleFilter() {
        // フィルターパラメータを作成
        Map<String, String[]> params = new HashMap<>();
        params.put("publish", new String[] { "1" });  // 公開ステータスが1のレコードをフィルタリング

        return params; 
    } 
}
