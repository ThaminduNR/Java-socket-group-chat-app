package lk.ijse.groupChat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    public TextField txtUserName;

    public void loginOnAction(ActionEvent actionEvent) {
        if (txtUserName.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Empty User Name", ButtonType.OK).show();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("../view/ChatForm.fxml"));
        Parent parent = null;
        try{
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChatFormController controller = fxmlLoader.getController();
        controller.setUserName(txtUserName.getText());

        Stage stage = new Stage();
        stage.setTitle(txtUserName.getText());
        stage.setScene(new Scene(parent));
        stage.show();
        txtUserName.clear();

    }
}
