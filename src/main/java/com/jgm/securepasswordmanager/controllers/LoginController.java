package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginStatusLabel;

    private AuthenticationService theAuthenticationService;



    public void initialize() {
        theAuthenticationService = new AuthenticationService();

    }

    @FXML
    protected void handleRegisterButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/create_new_account.fxml");
    }

//    @FXML
//    protected void handleLoginButtonClicked() {
//        String username = userNameField.getText().trim();
//        String password = passwordField.getText().trim();
//
//        User theLoadedUser = theAuthenticationService.login(username, password);
//
//        if (theLoadedUser != null) {
//            try {
//                // Load the password_manager.fxml file
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jgm/securepasswordmanager/password_manager.fxml"));
//                Parent root = loader.load();
//
//                // Retrieve the PasswordManagerController
//                PasswordManagerController passwordManagerController = loader.getController();
//
//                // Set the loaded user in the PasswordManagerController
//                passwordManagerController.setUser(theLoadedUser);
//
//                // Get the current stage (from a component in the current scene, e.g., the login button)
//                Stage stage = (Stage) loginButton.getScene().getWindow();
//
//                // Set the scene to the stage with the loaded FXML
//                stage.setScene(new Scene(root));
//                stage.show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                // Handle the exception, maybe show an error dialog to the user.
//            }
//        } else {
//            System.out.println("Failed to login user");
//        }
//    }

    @FXML
    protected void handleLoginButtonClicked(ActionEvent event) {
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        User theLoadedUser = theAuthenticationService.login(username, password);

        if (theLoadedUser != null) {

            loginStatusLabel.setText("    Login successful.\n     Loading your secure password vault...");
            loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");

            pauseAndLoadController(event, "/com/jgm/securepasswordmanager/password_manager.fxml", 4);
        } else {
            loginStatusLabel.setText("    Login failure.\n     Invalid username or password.");
            loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
        }
    }

    private void pauseAndLoadController(ActionEvent event, String fxmlPath, double pauseSeconds) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> loadController(event, fxmlPath));
        pause.play();
    }


    private void loadController(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleForgotPasswordButtonClicked() {
        System.out.println(" ");
    }
}