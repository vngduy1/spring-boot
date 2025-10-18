package dvn.local.dvnjs.modules.users.services.impl;

import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import dvn.local.dvnjs.services.BaseService;
import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;
import dvn.local.dvnjs.modules.users.resources.UserResource;

@Service
public class UserService extends BaseService implements UserServiceInterface {

    @Override
    public LoginResource login(LoginRequest request) {
        try {
            
            // String email = request.getEmail();
            // String password = request.getPassword();

            String token = "random_token";
            UserResource user = new UserResource(1L, "dvn@gmail.com");

            return new LoginResource(token, user);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("エラー発生しました。");
        }
    }

    
}
