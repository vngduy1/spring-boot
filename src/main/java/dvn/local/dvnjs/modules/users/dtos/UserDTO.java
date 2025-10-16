package dvn.local.dvnjs.modules.users.dtos;

public class UserDTO {
    private final Long id;
    private String email;

    public UserDTO(
        Long id,
        String email
    ) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    
}
