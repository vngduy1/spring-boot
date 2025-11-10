package dvn.local.dvnjs.modules.users.mappers;

import org.mapstruct.Mapper;

import dvn.local.dvnjs.mappers.BaseMapper;
import dvn.local.dvnjs.modules.users.entities.Permission;
import dvn.local.dvnjs.modules.users.requests.Permission.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.Permission.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.PermissionResource;

// Mapperインターフェース
@Mapper(componentModel = "spring")  
public interface PermissionMapper extends BaseMapper<Permission, PermissionResource, StoreRequest, UpdateRequest> {
    
}
