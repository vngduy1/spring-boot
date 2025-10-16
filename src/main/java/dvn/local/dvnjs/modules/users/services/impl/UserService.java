package dvn.local.dvnjs.modules.users.services.impl;

import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import dvn.local.dvnjs.services.BaseService;
import dvn.local.dvnjs.modules.users.dtos.LoginRequest;
import dvn.local.dvnjs.modules.users.dtos.LoginResponse;
import dvn.local.dvnjs.modules.users.dtos.UserDTO;

@Service
public class UserService extends BaseService implements UserServiceInterface {

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            
            // String email = request.getEmail();
            // String password = request.getPassword();

            String token = "random_token";
            UserDTO user = new UserDTO(1L, "dvn@gmail.com");

            return new LoginResponse(token, user);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("エラー発生しました。");
        }
    }

    
}
