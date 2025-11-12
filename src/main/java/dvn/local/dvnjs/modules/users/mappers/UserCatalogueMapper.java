package dvn.local.dvnjs.modules.users.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dvn.local.dvnjs.annotations.BaseMapperAnnotation;
import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;

// Mapperインターフェース
@Mapper(componentModel = "spring")  
public interface UserCatalogueMapper
        extends BaseMapper<UserCatalogue, UserCatalogueResource, StoreRequest, UpdateRequest> {
    
    @Override
    @BaseMapperAnnotation
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE)
    UserCatalogue toEntity(StoreRequest createRequest);

    @Override
    @BaseMapperAnnotation
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromResource(UpdateRequest updateRequest,@MappingTarget UserCatalogue entity);
}
