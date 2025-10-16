package dvn.local.dvnjs.modules.users.dtos;

public class LoginResponse {
    
    private final String token;
    private final UserDTO user;

    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserDTO getUser() {
        return user;
    }
}
