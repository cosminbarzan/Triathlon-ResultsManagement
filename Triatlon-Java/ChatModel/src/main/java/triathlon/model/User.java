package triathlon.model;

import java.io.Serializable;

public class User extends Entity<Integer> implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Stage stage;

    public User() {
    }

    public User(String firstName, String lastName, String username, String password, Stage stage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.stage = stage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() + " " + firstName + " " + lastName + " " + username + " idStage: " + stage.getId();
    }
}
