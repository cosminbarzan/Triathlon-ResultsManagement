import triathlon.model.Stage;
import triathlon.model.User;
import triathlon.network.utils.AbstractServer;
import triathlon.network.utils.RpcConcurrentServer;
import triathlon.network.utils.ServerException;

import triathlon.persistence.repository.hbm.UserHbmRepository;
import triathlon.persistence.repository.jdbc.triatlon.ParticipantDBRepository;
import triathlon.persistence.repository.jdbc.triatlon.ResultDBRepository;
import triathlon.persistence.repository.jdbc.triatlon.StageDBRepository;
import triathlon.persistence.ParticipantRepository;
import triathlon.persistence.ResultRepository;
import triathlon.persistence.StageRepository;
import triathlon.persistence.UserRepository;

import triathlon.server.ServicesImpl;

import triathlon.services.IServices;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class StartRpcServer {
    static SessionFactory sessionFactory;
    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close(){
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }

    }

    public static void testUserHbm(UserRepository userRepo){
        //User user = userRepo.findOne(1);
        //System.out.println(user);
        List<User> users = (List<User>) userRepo.findAll();
    }

    private static int defaultPort=55555;
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

        //UserRepository userRepo = new UserDBRepository(serverProps);

        initialize();
        UserRepository userHbmRepo = new UserHbmRepository(sessionFactory);
        testUserHbm(userHbmRepo);

        IServices serverImpl = new ServicesImpl(participantRepo, resultRepo, stageRepo, userHbmRepo);

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
        AbstractServer server = new RpcConcurrentServer(serverPort, serverImpl);

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
