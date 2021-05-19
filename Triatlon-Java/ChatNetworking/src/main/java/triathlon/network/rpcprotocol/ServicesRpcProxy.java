package triathlon.network.rpcprotocol;


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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    public User login(User user, IObserver client) throws TriatlonException {
        initializeConnection();

        UserDTO udto = DTOUtils.getDTO(user);
        Request req = new Request.Builder().type(RequestType.LOGIN).data(udto).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.OK){
            this.client = client;
            System.out.println(client);
            System.out.println("PPPPPPPPRRRRRRROOOOOOOXXXXXXYYYYYYYY");
            User user1 = DTOUtils.getFromDTO((UserDTO) response.data());
            System.out.println(user1);
            return user1;
        }
        if (response.type() == ResponseType.ERROR){
            String err = response.data().toString();
            closeConnection();
            throw new TriatlonException(err);
        }
        return null;
    }

    public void addResult(Result result) throws TriatlonException {
        ResultDTO resultDTO = DTOUtils.getDTO(result);

        Request req = new Request.Builder().type(RequestType.ADD_RESULT).data(resultDTO).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String err = response.data().toString();
            throw new TriatlonException(err);
        }
    }

    public void logout(User user, IObserver client) throws TriatlonException {
        UserDTO udto = DTOUtils.getDTO(user);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        System.out.println( "close connection");

        if (response.type() == ResponseType.ERROR){
            String err = response.data().toString();
            throw new TriatlonException(err);
        }
    }

    @Override
    public Participant[] getParticipantsByScore(Integer idStage) throws TriatlonException {
        Request request = new Request.Builder().type(RequestType.GET_PARTICIPANTS_BY_SCORE).data(idStage).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new TriatlonException(err);
        }

        Participant[] participants = (Participant[]) response.data();

        return participants;
    }

    @Override
    public ParticipantDTO[] getParticipantsAndScore() throws TriatlonException {
        Request request = new Request.Builder().type(RequestType.GET_PARTICIPANTS_AND_SCORE).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new TriatlonException(err);
        }

        ParticipantDTO[] participants = (ParticipantDTO[]) response.data();

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

    private void sendRequest(Request request) throws TriatlonException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new TriatlonException("Error sending object " + e);
        }

    }

    private Response readResponse() throws TriatlonException {
        Response response = null;
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
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw = new Thread(new ServicesRpcProxy.ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){

        if (response.type() == ResponseType.NEW_RESULT){
            Result result = DTOUtils.getFromDTO((ResultDTO)response.data());

            try {
                client.resultAdded(result);
            }
            catch (TriatlonException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.NEW_RESULT;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response = input.readObject();

                    System.out.println("response received " + response);

                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
