package dvn.local.dvnjs.modules.users.services.interfaces;

import dvn.local.dvnjs.modules.users.requests.LoginRequest;

public interface UserServiceInterface {

    Object authenticate(LoginRequest request);

    
    
}
