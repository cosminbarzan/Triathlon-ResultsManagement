package triathlon.client.gui.triatlon;

import triathlon.model.User;

import triathlon.services.IServices;
import triathlon.services.TriatlonException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    double x = 0;
    double y = 0;

    private IServices server;
    private DashboardController dashboardCtrl;
    private User crtUser;

    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private AnchorPane anchor_pane;


    public void setServer(IServices s){
        server = s;
    }

    @FXML
    void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    public void handleLogin(MouseEvent event) throws IOException {
        String username = textFieldUsername.getText();
        String passwd = textFieldPassword.getText();

        triathlon.model.Stage stage = new triathlon.model.Stage();
        stage.setId(2);

        //crtUser = new User("", "", username, passwd, stage);

        User user = null;
        if (username.equals("vladpopescu"))
        {
            stage.setId(2);
            user = new User("Vlad", "Popescu", username, passwd, stage);
            user.setId(1);
        }

        if (username.equals("sergiudavid"))
        {
            stage.setId(1);
            user = new User("Sergiu", "David", username, passwd, stage);
            user.setId(2);
        }

        if (username.equals("andreiionescu"))
        {
            stage.setId(3);
            user = new User("Andrei", "Ionescu", username, passwd, stage);
            user.setId(3);
        }

        crtUser = user;

        //
//        FXMLLoader dashLoader = new FXMLLoader();
//
//        dashLoader.setLocation(getClass().getResource("/Dashboard.fxml"));
//
//        dashLoader.load();
//
//        DashboardController dashboardController = dashLoader.getController();
//
//
//        dashboardCtrl = dashboardController;

        DashboardController dashboardController = new DashboardController();
        dashboardController.setServer1(server);
        dashboardController.setUser1(crtUser);

        FXMLLoader dashLoader = new FXMLLoader();

        System.out.println(getClass().getResource("/Dashboard.fxml"));
        dashLoader.setLocation(getClass().getResource("/Dashboard.fxml"));

        dashLoader.setController(dashboardController);

        AnchorPane fxml = dashLoader.load();


        try{
            crtUser = server.login(crtUser, dashboardController);
            System.out.println("################################");
            System.out.println(crtUser);
            System.out.println("################################");
            // Util.writeLog("User succesfully logged in "+crtUser.getId());

            goToDashboard(crtUser.getStage().getId(), fxml);

        }   catch (TriatlonException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP triathlon");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void goToDashboard(Integer id, AnchorPane fxml) throws IOException {
//        DashboardController.setServer(server);
//        DashboardController.setUser(crtUser);
//
//        AnchorPane fxml = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));

//        DashboardController dashboardController = new DashboardController();
//        dashboardController.setServer1(server);
//        dashboardController.setUser1(crtUser);
//
//        FXMLLoader dashLoader = new FXMLLoader();
//
//        dashLoader.setLocation(getClass().getResource("/Dashboard.fxml"));
//
//        AnchorPane fxml = dashLoader.load();
//
//        dashLoader.setController(dashboardController);

        anchor_pane.getChildren().removeAll();

        anchor_pane.getChildren().setAll(fxml);
    }
}
