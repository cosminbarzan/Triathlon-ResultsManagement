import triathlon.network.utils.ProtobuffConcurrentServer;
import triathlon.network.utils.ServerException;

import triathlon.persistence.repository.jdbc.triatlon.ParticipantDBRepository;
import triathlon.persistence.repository.jdbc.triatlon.ResultDBRepository;
import triathlon.persistence.repository.jdbc.triatlon.StageDBRepository;
import triathlon.persistence.repository.jdbc.triatlon.UserDBRepository;
import triathlon.persistence.ParticipantRepository;
import triathlon.persistence.ResultRepository;
import triathlon.persistence.StageRepository;
import triathlon.persistence.UserRepository;

import triathlon.server.ServicesImpl;

import triathlon.services.IServices;

import java.io.IOException;
import java.util.Properties;

public class StartProtobuffServer {
    private static int defaultPort = 55555;
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        }
        catch (IOException e) {
            System.err.println("Cannot find server.properties " + e);
            return;
        }

        ParticipantRepository participantRepo = new ParticipantDBRepository(serverProps);
        ResultRepository resultRepo = new ResultDBRepository(serverProps);
        StageRepository stageRepo = new StageDBRepository(serverProps);
        UserRepository userRepo = new UserDBRepository(serverProps);

        IServices serverImpl = new ServicesImpl(participantRepo, resultRepo, stageRepo, userRepo);

        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }
        catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);

        //
        ProtobuffConcurrentServer server = new ProtobuffConcurrentServer(serverPort, serverImpl);

        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}