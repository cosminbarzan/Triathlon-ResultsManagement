package triathlon.model;

import java.io.Serializable;

public class Stage extends Entity<Integer> implements Serializable {
    private String name;

    public Stage(String name) {
        this.name = name;
    }

    public Stage() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
