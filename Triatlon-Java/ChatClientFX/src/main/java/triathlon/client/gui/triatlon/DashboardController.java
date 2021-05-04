package triathlon.client.gui.triatlon;

import triathlon.client.gui.Util;
import triathlon.model.*;
import triathlon.services.IObserver;
import triathlon.services.IServices;
import triathlon.services.TriatlonException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import triathlon.model.*;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements IObserver, Initializable {
    private static IServices server;
    private static User user;

    @FXML
    private Label refereeName;
    @FXML
    private TableView<ParticipantDTO> tableView1;
    @FXML
    private TableColumn<ParticipantDTO, String> firstNameColumn;
    @FXML
    private TableColumn<ParticipantDTO, String> lastNameColumn;
    @FXML
    private TableColumn<ParticipantDTO, String> scoreColumn;
    @FXML
    private TableColumn<ParticipantDTO, String> idColumn;

    @FXML
    private TableView<Participant> tableView2;
    @FXML
    private TableColumn<Participant, String> firstNameColumn2;
    @FXML
    private TableColumn<Participant, String> lastNameColumn2;

    @FXML
    private TextField scoreField;

    @FXML
    private AnchorPane anchor_pane2;

    ObservableList<ParticipantDTO> participantsList = FXCollections.observableArrayList();

    ObservableList<Participant> participantsList2 = FXCollections.observableArrayList();

    public static void setServer(IServices s) {
        server = s;
    }

    public static void setUser(User u) {
        user = u;
    }

    public void setServer1(IServices s) {
        server = s;
    }

    public void setUser1(User u) {
        user = u;
    }

    private void populateTable1() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        tableView1.setItems(participantsList);
    }

    private void populateTable2() {
        firstNameColumn2.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn2.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableView2.setItems(participantsList2);
    }

    private void initLists(Integer idStage) throws TriatlonException {
        participantsList.clear();
        participantsList.setAll(server.getParticipantsAndScore());

        participantsList2.clear();
        participantsList2.setAll(server.getParticipantsByScore(idStage));

    }

    public void addResult(MouseEvent event) throws TriatlonException {
        Double score = Double.parseDouble(scoreField.getText());

        Integer id = tableView1.getSelectionModel().getSelectedItem().getId();

        ParticipantDTO participantDTO = tableView1.getSelectionModel().getSelectedItem();
        Participant participant = new Participant(participantDTO.getFirstName(), participantDTO.getLastName());
        participant.setId(participantDTO.getId());

        Stage stage = user.getStage();
        Result result = new Result(participant, stage, score);

        try {
            server.addResult(result);
            scoreField.clear();
        }
        catch (TriatlonException e) {
            Util.showWarning("Communication error","Your server probably closed connection");
        }

    }

    public void handlePopulate(MouseEvent event) {
        Integer idStage = user.getStage().getId();
        try {
            initLists(idStage);
        } catch (TriatlonException e) {
            e.printStackTrace();
        }
        populateTable1();
        populateTable2();
    }

    public void handleLogout(MouseEvent event) {
//        FXMLLoader loginLoader = new FXMLLoader();
//        loginLoader.setLocation(getClass().getResource("/views/Login.fxml"));
//        Parent fxml = loginLoader.load();
//
//        anchor_pane2.getChildren().removeAll();
//
//        anchor_pane2.getChildren().setAll(fxml);
//
//        LoginController loginController = loginLoader.getController();
//        loginController.setService(service);

        try {
            server.logout(user, this);
        } catch (TriatlonException e) {
            System.out.println("Logout error " + e);
        }
    }

    @Override
    public void resultAdded(Result result) throws TriatlonException {
        Platform.runLater(() -> {
            Integer idStage = user.getStage().getId();
            try {
                initLists(idStage);
            } catch (TriatlonException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Integer idStage = user.getStage().getId();
//        try {
//            //initLists(idStage);
//        } catch (TriatlonException e) {
//            e.printStackTrace();
//        }
        refereeName.setText(user.getFirstName() + " " + user.getLastName());
        populateTable1();
        populateTable2();
        scoreField.setDisable(true);
        tableView1.getSelectionModel().selectedItemProperty().addListener((this::changed1));
    }

    private void changed1(ObservableValue<? extends ParticipantDTO> observable, ParticipantDTO oldValue, ParticipantDTO newValue) {
        scoreField.setDisable(false);
    }
}
