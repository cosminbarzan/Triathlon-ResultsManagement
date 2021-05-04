package triathlon.server;


import triathlon.model.Participant;
import triathlon.model.ParticipantDTO;
import triathlon.model.Result;
import triathlon.model.User;

import triathlon.persistence.UserRepository;
import triathlon.persistence.ParticipantRepository;
import triathlon.persistence.ResultRepository;
import triathlon.persistence.StageRepository;

import triathlon.services.IObserver;
import triathlon.services.IServices;
import triathlon.services.TriatlonException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServicesImpl implements IServices {

    private ParticipantRepository participantRepo;
    private ResultRepository resultRepo;
    private StageRepository stageRepo;
    private UserRepository userRepo;

    private Map<Integer, IObserver> loggedClients;


    public ServicesImpl(ParticipantRepository participantRepo, ResultRepository resultRepo, StageRepository stageRepo, UserRepository userRepo) {
        this.participantRepo = participantRepo;
        this.resultRepo = resultRepo;
        this.stageRepo = stageRepo;
        this.userRepo = userRepo;
        loggedClients = new ConcurrentHashMap<>();;
    }

    public synchronized User login(User user, IObserver client) throws TriatlonException {
        Integer idUser = userRepo.verifyAccount(user.getUsername(), user.getPassword());
        User userR = userRepo.findOne(idUser);
        System.out.println("userR " + userR.getId());
        if (userR!=null){
            System.out.println( "user " + user.getId());
            if(loggedClients.get(userR.getId()) != null)
                throw new TriatlonException("User already logged in.");
            loggedClients.put(userR.getId(), client);
            //notifyFriendsLoggedIn(user);
        }else
            throw new TriatlonException("Authentication failed.");

        System.out.println("!!!!!!!!!!!!!!!!!!");
        System.out.println(userR);
        System.out.println("!!!!!!!!!!!!!!!!!!");
        return userR;
    }


    public synchronized void addResult(Result result) throws TriatlonException {
        resultRepo.save(result);
        System.out.println("Added result. Now going to notify logged users...");

        notifyResultAdded(result);
    }

    private final int defaultThreadsNo=5;
    private void notifyResultAdded(Result result) throws TriatlonException {
        System.out.println("Notifying result added...");

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(Map.Entry<Integer, IObserver> entry : loggedClients.entrySet()) {
            IObserver client = entry.getValue();
            if(client != null) {
                executor.execute(() -> {
                    try {
                        System.out.println("Notifying [" + entry.getKey() + "] result [" + result.getId() + "] added.");
                        client.resultAdded(result);
                    }
                    catch (TriatlonException e) {
                        System.err.println("Error notifying added result " + e);
                    }
                });
            }
        }
        executor.shutdown();
    }


    public synchronized void logout(User user, IObserver client) throws TriatlonException {
        System.out.println("IIIIIIIIIIIIIIIIIIIIIIIII");
        System.out.println(user);
        IObserver localClient = loggedClients.remove(user.getId());
        if (localClient == null)
            throw new TriatlonException("User " + user.getId() + " is not logged in.");
    }


    public synchronized Participant[] getParticipantsByScore(Integer idStage) throws TriatlonException{
        System.out.println("Get participants by score from stage : " + idStage);
        List<Participant> participants = (List<Participant>) participantRepo.getParticipantsByScore(idStage);

        return participants.toArray(new Participant[participants.size()]);
    }

    public synchronized ParticipantDTO[] getParticipantsAndScore() throws TriatlonException{
        System.out.println("Get participants and score");
        List<ParticipantDTO> participants = new ArrayList<>();

        participantRepo.findAll().forEach(participant -> {
            Double score = participantRepo.getParticipantScore(participant.getId());
            ParticipantDTO p = new ParticipantDTO(participant.getId(), participant.getFirstName(), participant.getLastName(), score);
            participants.add(p);
        });

        return participants.toArray(new ParticipantDTO[participants.size()]);
    }
}
