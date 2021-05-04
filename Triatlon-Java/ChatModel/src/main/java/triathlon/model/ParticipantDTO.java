package triathlon.model;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private Integer id;
    private String firstName;
    private String lastName;
    private Double score;

    public ParticipantDTO(Integer id, String firstName, String lastName, Double score) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
