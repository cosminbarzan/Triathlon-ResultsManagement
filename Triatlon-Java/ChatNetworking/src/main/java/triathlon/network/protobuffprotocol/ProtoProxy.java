package triathlon.network.protobuffprotocol;


import triathlon.model.Participant;
import triathlon.model.ParticipantDTO;
import triathlon.model.Result;
import triathlon.model.User;
import triathlon.network.dto.DTOUtils;
import triathlon.network.dto.ResultDTO;
import triathlon.network.dto.UserDTO;

import triathlon.services.TriatlonException;
import triathlon.services.IObserver;

import triathlon.services.IServices;

import java.io.*;
import java.net.Socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static triathlon.network.protobuffprotocol.TriathlonProtobufs.Response.ResponseType.NEW_RESULT;


public class ProtoProxy implements IServices {
    private String host;
    private int port;

    private IObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private BlockingQueue<TriathlonProtobufs.Response> qresponses;
    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<TriathlonProtobufs.Response>();
    }

    public User login(User user, IObserver client) throws TriatlonException {
        initializeConnection();

        UserDTO udto = DTOUtils.getDTO(user);
        sendRequest(ProtoUtils.createLoginRequest(udto));
        TriathlonProtobufs.Response response = readResponse();

        if (response.getType() == TriathlonProtobufs.Response.ResponseType.OK){
            this.client = client;
            TriathlonProtobufs.UserDTO u = response.getUser();
            UserDTO userDTO = new UserDTO(Math.toIntExact(u.getId()), u.getFirstName(), u.getLastName(), u.getUsername(), u.getPassword(), Math.toIntExact(u.getIdStage()));
            User user1 = DTOUtils.getFromDTO(userDTO);
            return user1;
        }
        if (response.getType() == TriathlonProtobufs.Response.ResponseType.ERROR){
            String errorText = ProtoUtils.getError(response);
            closeConnection();
            throw new TriatlonException(errorText);
        }

        return null;
    }

    public void addResult(Result result) throws TriatlonException {
        ResultDTO resultDTO = DTOUtils.getDTO(result);

        sendRequest(ProtoUtils.createAddResultRequest(resultDTO));
        TriathlonProtobufs.Response response = readResponse();

        if (response.getType() == TriathlonProtobufs.Response.ResponseType.ERROR){
            String errorText = ProtoUtils.getError(response);
            throw new TriatlonException(errorText);
        }
    }

    public void logout(User user, IObserver client) throws TriatlonException {
        UserDTO udto = DTOUtils.getDTO(user);

        sendRequest(ProtoUtils.createLogoutRequest(udto));
        TriathlonProtobufs.Response response = readResponse();

        closeConnection();

        if (response.getType() == TriathlonProtobufs.Response.ResponseType.ERROR){
            String errorText = ProtoUtils.getError(response);
            throw new TriatlonException(errorText);
        }
    }

    @Override
    public Participant[] getParticipantsByScore(Integer idStage) throws TriatlonException {
        sendRequest(ProtoUtils.createGetParticipantsByScoreRequest(idStage));
        TriathlonProtobufs.Response response = readResponse();

        if (response.getType() == TriathlonProtobufs.Response.ResponseType.ERROR){
            String errorText = ProtoUtils.getError(response);
            throw new TriatlonException(errorText);
        }

        Participant[] participants = ProtoUtils.getParticipantsByScore(response);

        return participants;
    }

    @Override
    public ParticipantDTO[] getParticipantsAndScore() throws TriatlonException {
        sendRequest(ProtoUtils.createGetParticipantsAndScoreRequest());
        TriathlonProtobufs.Response response = readResponse();

        if (response.getType() == TriathlonProtobufs.Response.ResponseType.ERROR){
            String errorText = ProtoUtils.getError(response);
            throw new TriatlonException(errorText);
        }

        ParticipantDTO[] participants = ProtoUtils.getParticipantsAndScore(response);

        return participants;
    }


    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(TriathlonProtobufs.Request request) throws TriatlonException {
        try {
            System.out.println("Sending request ..." + request);

            request.writeDelimitedTo(output);
            output.flush();

            System.out.println("Request sent.");

        } catch (IOException e) {
            throw new TriatlonException("Error sending object " + e);
        }

    }

    private TriathlonProtobufs.Response readResponse() throws TriatlonException {
        TriathlonProtobufs.Response response = null;
        try{
            response = qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws TriatlonException {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            //output.flush();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(TriathlonProtobufs.Response response){

        if (response.getType() == NEW_RESULT){
            Result result = ProtoUtils.getResult(response);

            try {
                client.resultAdded(result);
            }
            catch (TriatlonException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(TriathlonProtobufs.Response.ResponseType type){
        return type == NEW_RESULT;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    TriathlonProtobufs.Response response = TriathlonProtobufs.Response.parseDelimitedFrom(input);
                    System.out.println("response received " + response);

                    if (isUpdate(response.getType())){
                        handleUpdate(response);
                    }else{

                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
