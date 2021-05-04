package triathlon.network.dto;


import java.io.Serializable;

public class UserDTO implements Serializable {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Integer idStage;

    public UserDTO(Integer id, String firstName, String lastName, String username, String password, Integer idStage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.idStage = idStage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getIdStage() {
        return idStage;
    }

    @Override
    public String toString() {
        return "UserDTO[" + username + ' ' + password + "]";
    }
}
