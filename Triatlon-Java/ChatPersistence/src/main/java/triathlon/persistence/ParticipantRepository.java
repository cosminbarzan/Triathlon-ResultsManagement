package triathlon.persistence;

import triathlon.model.Participant;

public interface ParticipantRepository extends Repository<Integer, Participant> {
    Iterable<Participant> getParticipantsByScore(Integer idStage);

    Double getParticipantScore(Integer id);
}
