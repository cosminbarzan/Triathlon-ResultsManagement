package triathlon.model;

import java.io.Serializable;

public class Participant extends Entity<Integer> implements Identifiable<Integer>, Serializable {
    private String firstName;
    private String lastName;

    public Participant(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Participant() {}

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

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer integer) {
        super.setId(integer);
    }

    @Override
    public String toString() {
        return "Id = " + getId() + " " + firstName + " " + lastName;
    }
}
