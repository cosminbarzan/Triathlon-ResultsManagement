package triathlon.model;

public class Result extends Entity<Integer>{
    private Participant participant;
    private Stage stage;
    private Double score;

    public Result(Participant participant, Stage stage, Double score) {
        this.participant = participant;
        this.stage = stage;
        this.score = score;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
