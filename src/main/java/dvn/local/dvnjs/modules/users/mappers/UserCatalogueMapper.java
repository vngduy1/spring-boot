package dvn.local.dvnjs.modules.users.mappers;

import org.mapstruct.Mapper;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;

// Mapperインターフェース
@Mapper(componentModel = "spring")  
public interface UserCatalogueMapper extends BaseMapper<UserCatalogue, UserCatalogueResource, StoreRequest, UpdateRequest> {
    
}
