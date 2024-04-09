package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import com.jgm.securepasswordmanager.services.LogParserService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginStatusLabel;

    private AuthenticationService theAuthenticationService;

    public void initialize() {
        theAuthenticationService = new AuthenticationService();

    }

    @FXML
    protected void handleRegisterButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/create_new_account.fxml", "New Account");
    }

    @FXML
    protected void handleLoginButtonClicked(ActionEvent event) {
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        User theLoadedUser = theAuthenticationService.login(username, password);

        if (theLoadedUser != null) {
            if (!theLoadedUser.getHasRegisteredTwoFactorAuthentication()) {
                loginStatusLabel.setText("Two factor authentication registration required.\nProceeding to two factor authentication setup...");
                loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
                pauseAndLoadTwoFactorSetupController(event, "/com/jgm/securepasswordmanager/two_factor_setup.fxml",
                        4, theLoadedUser);
            } else {
                pauseAndLoadTwoFactorVerificationController(event, "/com/jgm/securepasswordmanager/two_factor_verification.fxml", 0, theLoadedUser);
                LogParserService.appendLog(new LogEntry("INFO", "Login success. User: " + username));

            }

        } else {
            loginStatusLabel.setText("    Login failure.\n     Invalid username or password.");
            loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
            LogParserService.appendLog(new LogEntry("INFO", "Login failure. User: " + username));
        }
    }


    private void pauseAndLoadTwoFactorVerificationController(ActionEvent event, String fxmlPath, double pauseSeconds, User theNewUser) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                TwoFactorVerificationController twoFactorVerificationController = loader.getController();
                twoFactorVerificationController.setUser(theNewUser);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Two Factor Verification");
                stage.setScene(scene);

                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }



    private void pauseAndLoadTwoFactorSetupController(ActionEvent event, String fxmlPath, double pauseSeconds, User newUser) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                TwoFactorSetupController twoFactorSetupController = loader.getController();
                twoFactorSetupController.setUser(newUser); // Make sure to implement this method in TwoFactorSetupController
                twoFactorSetupController.generateAndDisplayQRCode(); // Now this can be called since the user has been set

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Two Factor Authentication Setup");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }


    private void loadController(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
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