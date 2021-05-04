package triathlon.network.dto;

import triathlon.model.Entity;

import java.io.Serializable;

public class ResultDTO extends Entity<Integer> implements Serializable {
    private Integer participantId;
    private Integer stageId;
    private Double score;

    public ResultDTO(Integer participantId, Integer stageId, Double score) {
        this.participantId = participantId;
        this.stageId = stageId;
        this.score = score;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public Integer getStageId() {
        return stageId;
    }

    public Double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ResultDTO[ participant: " + participantId + " stage: " + stageId + " score: " + score + "]";
    }
}
