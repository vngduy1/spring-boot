package dvn.local.dvnjs.modules.users.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

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
                .publish(22)
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
                .build()
        );
    }
}
