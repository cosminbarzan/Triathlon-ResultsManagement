package triathlon.network.rpcprotocol;


import triathlon.model.Participant;
import triathlon.model.ParticipantDTO;
import triathlon.model.Result;
import triathlon.model.User;
import triathlon.network.dto.DTOUtils;
import triathlon.network.dto.UserDTO;
import triathlon.network.dto.ResultDTO;

import triathlon.services.TriatlonException;

import triathlon.services.IObserver;
import triathlon.services.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;


public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public ClientRpcReflectionWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request)request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    public void resultAdded(Result result) throws TriatlonException {
        ResultDTO resultDTO = DTOUtils.getDTO(result);
        Response response = new Response.Builder().type(ResponseType.NEW_RESULT).data(resultDTO).build();
        System.out.println("Result added " + result);

        try {
            sendResponse(response);
        }
        catch (IOException e) {
            throw new TriatlonException("Sending error: " + e);
        }
    }


    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    //  private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private Response handleRequest(Request request){
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response)method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request){
        System.out.println("Login request ..." + request.type());

        UserDTO udto = (UserDTO)request.data();
        User user = DTOUtils.getFromDTO(udto);
        System.out.println("WWWWOOOOORRRRRKKKKEEEERRRR1111");
        System.out.println(udto);

        User loggedUser = null;
        try {
            loggedUser = server.login(user, this);
            UserDTO userDTO = DTOUtils.getDTO(loggedUser);
            System.out.println("WWWWOOOOORRRRRKKKKEEEERRRR22222");
            System.out.println(userDTO);
            return new Response.Builder().type(ResponseType.OK).data(userDTO).build();
        } catch (TriatlonException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request){
        System.out.println("Logout request...");

        UserDTO udto = (UserDTO)request.data();
        User user = DTOUtils.getFromDTO(udto);
        try {
            server.logout(user, this);
            connected = false;
            return okResponse;

        } catch (TriatlonException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_RESULT(Request request){
        System.out.println("AddResultRequest ...");

        ResultDTO resultDTO = (ResultDTO)request.data();
        Result result = DTOUtils.getFromDTO(resultDTO);

        try {
            server.addResult(result);
            return okResponse;
        } catch  (TriatlonException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_PARTICIPANTS_BY_SCORE(Request request){
        System.out.println("GetParticipantsByScore Request ...");

        Integer idStage = (Integer) request.data();

        try {
            Participant[] participants = server.getParticipantsByScore(idStage);
            return new Response.Builder().type(ResponseType.GET_PARTICIPANTS_BY_SCORE).data(participants).build();
        }
        catch (TriatlonException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_PARTICIPANTS_AND_SCORE(Request request){
        System.out.println("GetParticipantsAndScore Request ...");

        try {
            ParticipantDTO[] participants = server.getParticipantsAndScore();
            return new Response.Builder().type(ResponseType.GET_PARTICIPANTS_AND_SCORE).data(participants).build();
        }
        catch (TriatlonException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response " + response);
        output.writeObject(response);
        output.flush();
    }
}
