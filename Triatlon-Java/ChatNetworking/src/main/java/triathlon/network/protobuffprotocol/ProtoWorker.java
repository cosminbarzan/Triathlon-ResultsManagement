package triathlon.network.protobuffprotocol;



import triathlon.model.Participant;
import triathlon.model.ParticipantDTO;
import triathlon.model.Result;
import triathlon.model.User;
import triathlon.network.dto.DTOUtils;
import triathlon.network.dto.ResultDTO;

import triathlon.network.rpcprotocol.Response;
import triathlon.network.rpcprotocol.ResponseType;

import triathlon.services.TriatlonException;

import triathlon.services.IObserver;
import triathlon.services.IServices;

import java.io.*;
import java.net.Socket;


public class ProtoWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;

    public ProtoWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = connection.getOutputStream();
            //output.flush();
            input = connection.getInputStream();
            connected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                System.out.println("Waiting requests ...");
                TriathlonProtobufs.Request request = TriathlonProtobufs.Request.    parseDelimitedFrom(input);
                System.out.println("Request received: "+request);

                TriathlonProtobufs.Response response = handleRequest(request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException e) {
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
        System.out.println("Result added " + result);
        try {
            ResultDTO resultDTO = DTOUtils.getDTO(result);
            sendResponse(ProtoUtils.createNewResultResponse(resultDTO));
        } catch (IOException e) {
            throw new TriatlonException("Sending error: " + e);
        }
    }


    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    //  private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private TriathlonProtobufs.Response handleRequest(TriathlonProtobufs.Request request) {
        TriathlonProtobufs.Response response = null;

        switch (request.getType()) {
            case LOGIN: {
                System.out.println("Login request ...");
                User user = ProtoUtils.getUser(request);

                try {
                    server.login(user, this);
                    return ProtoUtils.createOkResponse();
                } catch (TriatlonException e) {
                    connected = false;
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case LOGOUT: {
                System.out.println("Logout request ...");
                User user = ProtoUtils.getUser(request);

                try {
                    server.logout(user, this);
                    connected = false;
                    return ProtoUtils.createOkResponse();

                } catch (TriatlonException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case ADD_RESULT: {
                System.out.println("AddResultRequest ...");
                Result result = ProtoUtils.getResult(request);

                try {
                    server.addResult(result);
                    return ProtoUtils.createOkResponse();
                } catch (TriatlonException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GET_PARTICIPANTS_BY_SCORE: {
                System.out.println("GetParticipantsByScore Request ...");
                Integer idStage = ProtoUtils.getIdStage(request);

                try {
                    Participant[] participants = server.getParticipantsByScore(idStage);
                    return ProtoUtils.createGetParticipantsByScoreResponse(participants);
                } catch (TriatlonException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GET_PARTICIPANTS_AND_SCORE: {
                System.out.println("GetParticipantsAndScore Request ...");

                try {
                    ParticipantDTO[] participants = server.getParticipantsAndScore();
                    return ProtoUtils.createGetParticipantsAndScoreResponse(participants);
                } catch (TriatlonException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
        }

        return response;
    }

    private void sendResponse (TriathlonProtobufs.Response response) throws IOException {
        System.out.println("sending response " + response);
        response.writeDelimitedTo(output);
        output.flush();
    }
}
