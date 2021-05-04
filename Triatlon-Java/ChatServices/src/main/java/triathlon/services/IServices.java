package triathlon.services;

import triathlon.model.User;
import triathlon.model.Participant;
import triathlon.model.ParticipantDTO;
import triathlon.model.Result;


public interface IServices {
    User login(User user, IObserver client) throws TriatlonException;
    void addResult(Result result) throws TriatlonException;
    void logout(User user, IObserver client) throws TriatlonException;
    Participant[] getParticipantsByScore(Integer idStage) throws TriatlonException;
    ParticipantDTO[] getParticipantsAndScore() throws TriatlonException;

//    Participant[] getParticipantsByScore(Integer idStage) throws TriatlonException;
//    ParticipantDTO[] getParticipantsAndScore() throws TriatlonException;
}
