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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class CreateNewAccountController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailAddressField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label bottomLabel;


    private AuthenticationService theAuthenticationService;

    private User theNewUser;

    private StringBuilder theInformationalAlertMessage;


    @FXML
    private void initialize() {
        theAuthenticationService = new AuthenticationService();
        theNewUser = new User();
        theInformationalAlertMessage = new StringBuilder();
    }


    @FXML
    private void handleCancelButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login");
    }


    @FXML
    private void handleRegisterButtonClicked(ActionEvent event) {
        if (isAllInputValid()) {

            String userName = theNewUser.getUserName();
            String password = theNewUser.getPassword();

            boolean saveResult = theAuthenticationService.saveUser(theNewUser);

            if (saveResult) {
                theNewUser = theAuthenticationService.login(userName, password);
                bottomLabel.setText("Registration successful.\n Proceeding to two factor authentication setup...");
                bottomLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
                pauseAndLoadTwoFactorSetupController(event, "/com/jgm/securepasswordmanager/two_factor_setup.fxml",
                        4, theNewUser);
                LogParserService.appendLog(new LogEntry("INFO", "Account creation success. User: " + userName));

            } else {
                  System.out.println("Failed to register the user.");
                  displayErrorAlert("Registration Error","Registration Error", "System failed to register the user. Please try again.");
                LogParserService.appendLog(new LogEntry("SEVERE", "Account creation failure. User: " + userName));

            }

        } else {
            displayInformationalAlert("Invalid input", "Invalid input", theInformationalAlertMessage.toString());
        }
    }


    private boolean isAllInputValid() {
        // Clear previous error messages
        theInformationalAlertMessage.setLength(0);

        // Check each input individually
        boolean firstNameValid = isValidFirstName(firstNameField.getText().trim());
        boolean lastNameValid = isValidLastName(lastNameField.getText().trim());
        boolean emailAddressValid = isValidEmailAddress(emailAddressField.getText().trim());
        boolean userNameValid = isValidUserName(usernameField.getText().trim());
        boolean passwordsValid = isValidPasswordAndConfirmationPassword(passwordField.getText().trim(), confirmPasswordField.getText().trim());

        // Combine all validation checks
        return firstNameValid && lastNameValid && emailAddressValid && userNameValid && passwordsValid;

    }


    private boolean isValidFirstName(String firstName) {
        boolean isValid = theNewUser.setFirstName(firstName);
        if (!isValid) theInformationalAlertMessage.append("Invalid first name.\n");
        return isValid;
    }

    private boolean isValidLastName(String lastName) {
        boolean isValid = theNewUser.setLastName(lastName);
        if (!isValid) theInformationalAlertMessage.append("Invalid last name.\n");
        return isValid;
    }

    private boolean isValidEmailAddress(String emailAddress) {
        boolean isValid = theNewUser.setEmailAddress(emailAddress);
        if (!isValid) theInformationalAlertMessage.append("Invalid email address format.\n");
        return isValid;
    }

    private boolean isValidUserName(String userName) {
        boolean isValid = theNewUser.setUserName(userName);
        if (!isValid) theInformationalAlertMessage.append("Invalid username.\n");
        return isValid;
    }

    private boolean isValidPasswordAndConfirmationPassword(String password, String confirmationPassword) {
        boolean isValid = theNewUser.setPassword(password);
        if (!isValid || !password.equals(confirmationPassword)) {
            theInformationalAlertMessage.append("Passwords do not match or are invalid.\n");
            return false;
        }
        return true;
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
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Displays error popup.
    public void displayErrorAlert(String title, String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Displays informational popup.
    public void displayInformationalAlert(String title, String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
