// Defines the controller for handling login functionality in the Secure Password Manager application.
package com.jgm.securepasswordmanager.controllers;

// Import statements for required classes and packages.
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    // FXML-annotated fields for UI components in the corresponding FXML file.
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;

    // Instance of AuthenticationService to handle login logic.
    private AuthenticationService theAuthenticationService;

    // Initializes the controller. This method is automatically called after the FXML fields are injected.
    public void initialize() {
        // Instantiation of the AuthenticationService object.
        theAuthenticationService = new AuthenticationService();
    }

    // Handles the action of the Register button being clicked.
    @FXML
    protected void handleRegisterButtonClicked(ActionEvent event) {
        // Load the "Create New Account" view.
        loadController(event, "/com/jgm/securepasswordmanager/create_new_account.fxml", "New Account");
    }

    // Handles the action of the Login button being clicked.
    @FXML
    protected void handleLoginButtonClicked(ActionEvent event) {
        // Retrieve user input from text fields.
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        // Attempt to log the user in with the provided credentials.
        User theLoadedUser = theAuthenticationService.login(username, password);

        if (theLoadedUser != null) {
            // Check if two-factor authentication is set up for this account.
            if (!theLoadedUser.getHasRegisteredTwoFactorAuthentication()) {
                // Inform the user that two-factor authentication setup is required.
                loginStatusLabel.setText("Two factor authentication registration required.\nProceeding to two factor authentication setup...");
                loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
                // Pause and then load the Two Factor Authentication Setup controller.
                pauseAndLoadTwoFactorSetupController(event, "/com/jgm/securepasswordmanager/two_factor_setup.fxml",
                        4, theLoadedUser);
            } else {
                // User has two-factor authentication, proceed to verification.
                pauseAndLoadTwoFactorVerificationController(event, "/com/jgm/securepasswordmanager/two_factor_verification.fxml", 0, theLoadedUser);
                // Log successful login attempt.
                LogParserService.appendLog(new LogEntry("INFO", "Login success. User: " + username));
            }
        } else {
            // Display login failure message.
            loginStatusLabel.setText("    Login failure.\n     Invalid username or password.");
            loginStatusLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
            // Log failed login attempt.
            LogParserService.appendLog(new LogEntry("INFO", "Login failure. User: " + username));
        }
    }

    // Pauses for a specified time before loading the Two Factor Verification controller and scene.
    private void pauseAndLoadTwoFactorVerificationController(ActionEvent event, String fxmlPath, double pauseSeconds, User theNewUser) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> {
            try {
                // Load the FXML for two-factor verification and set the user.
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                TwoFactorVerificationController twoFactorVerificationController = loader.getController();
                twoFactorVerificationController.setUser(theNewUser);
                // Set up and show the stage.
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

    // Pauses for a specified time before loading the Two Factor Setup controller and scene.
    private void pauseAndLoadTwoFactorSetupController(ActionEvent event, String fxmlPath, double pauseSeconds, User newUser) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> {
            try {
                // Load the FXML for two-factor setup and set the new user.
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                TwoFactorSetupController twoFactorSetupController = loader.getController();
                twoFactorSetupController.setUser(newUser);
                // Call to display the QR code for two-factor authentication setup.
                twoFactorSetupController.generateAndDisplayQRCode();
                // Set up and show the stage.
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

    // Loads a specified FXML controller and sets the title for the scene.
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

    // Placeholder for handling forgotten password scenario.
    @FXML
    protected void handleForgotPasswordButtonClicked() {
        // Intentionally left blank or implement forgotten password functionality here.
        System.out.println("");
    }
}
