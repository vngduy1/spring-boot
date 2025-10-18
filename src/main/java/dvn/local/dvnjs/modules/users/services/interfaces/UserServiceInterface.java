package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;

public interface UserServiceInterface {

    LoginResource login(LoginRequest request);

    
    
}
