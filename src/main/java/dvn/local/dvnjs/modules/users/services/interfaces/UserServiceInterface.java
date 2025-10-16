package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.dtos.LoginRequest;
import dvn.local.dvnjs.modules.users.dtos.LoginResponse;

public interface UserServiceInterface {

    LoginResponse login(LoginRequest request);

    
    
}
