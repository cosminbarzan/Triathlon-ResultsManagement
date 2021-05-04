package triathlon.client;


import triathlon.client.gui.triatlon.*;

import triathlon.network.rpcprotocol.ServicesRpcProxy;
import triathlon.services.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import triathlon.client.gui.triatlon.LoginController;

import java.io.IOException;
import java.util.Properties;


public class StartRpcClientFX extends Application {
    public static Stage stage = null;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");

        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClientFX.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        //
        IServices server = new ServicesRpcProxy(serverIP, serverPort);
        //IServices server = new ProtoProxy(serverIP, serverPort);


        initView(primaryStage, server);
        this.stage = primaryStage;
        primaryStage.show();

    }

    private void initView(Stage primaryStage, IServices server) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();

        loginLoader.setLocation(getClass().getResource("/Login.fxml"));

        Scene scene = new Scene(loginLoader.load());

        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        LoginController loginController = loginLoader.getController();
        System.out.println(loginController);
        loginController.setServer(server);
    }

}


