//Package that groups related classes and interfaces
package com.jgm.securepasswordmanager.controllers;

// Imports necessary classes and packages
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

//Defines the controller for creating a new account.
public class CreateNewAccountController {

    /* Defined all allocated component parts of the JavaFX layout with their fx:id */
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

    // Service for verifying login credentials and saving new user information
    private AuthenticationService theAuthenticationService;

    // The new user being created
    private User theNewUser;

    // For storing messages to be shown to the user
    private StringBuilder theInformationalAlertMessage;

    /* This method is called after all @FXML annotated members have been injected. 
    Initializes controller state.*/
    @FXML
    private void initialize() {
        theAuthenticationService = new AuthenticationService();
        theNewUser = new User();
        theInformationalAlertMessage = new StringBuilder();
    }

    /* This function is called when 'Cancel' button is clicked. 
    It loads the login page controller */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login");
    }

    /* Handles clicking on the Register button */
    @FXML
    private void handleRegisterButtonClicked(ActionEvent event) {
        /* Validating user details, saving user if valid, and providing status feedback to the user*/
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

    /* Method to check if all data entered by the user is valid */
    private boolean isAllInputValid() {
        theInformationalAlertMessage.setLength(0);

        // Check each input field individually
        boolean firstNameValid = isValidFirstName(firstNameField.getText().trim());
        boolean lastNameValid = isValidLastName(lastNameField.getText().trim());
        boolean emailAddressValid = isValidEmailAddress(emailAddressField.getText().trim());
        boolean userNameValid = isValidUserName(usernameField.getText().trim());
        // Check if the password and the confirmation password match
        boolean passwordsValid = isValidPasswordAndConfirmationPassword(passwordField.getText().trim(), confirmPasswordField.getText().trim());

        // Combine all validity checks
        return firstNameValid && lastNameValid && emailAddressValid && userNameValid && passwordsValid;
    }

    /* Check if the entered firstName is valid.*/
    private boolean isValidFirstName(String firstName) {
        boolean isValid = theNewUser.setFirstName(firstName);
        if (!isValid) theInformationalAlertMessage.append("Invalid first name.\n");
        return isValid;
    }

    /* Check if the entered lastName is valid.*/
    private boolean isValidLastName(String lastName) {
        boolean isValid = theNewUser.setLastName(lastName);
        if (!isValid) theInformationalAlertMessage.append("Invalid last name.\n");
        return isValid;
    }

    /* Check the validity of the entered email address */
    private boolean isValidEmailAddress(String emailAddress) {
        boolean isValid = theNewUser.setEmailAddress(emailAddress);
        if (!isValid) theInformationalAlertMessage.append("Invalid email address format.\n");
        return isValid;
    }

    /* Check the validity of the entered username */
    private boolean isValidUserName(String userName) {
        boolean isValid = theNewUser.setUserName(userName);
        if (!isValid) theInformationalAlertMessage.append("Invalid username.\n");
        return isValid;
    }

    /* Check if entered password match with confirmation password and both are valid */
    private boolean isValidPasswordAndConfirmationPassword(String password, String confirmationPassword) {
        boolean isValid = theNewUser.setPassword(password);
        if (!isValid || !password.equals(confirmationPassword)) {
            theInformationalAlertMessage.append("Passwords do not match or are invalid.\n");
            return false;
        }
        return true;
    }

    /* This function is used to pause and load the controller for two factor setup */
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

    /* This function is used to load the controller for a given fxml and sets the title of the stage */
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

    // Function to display an error alert
    public void displayErrorAlert(String title, String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Function to display an informational alert
    public void displayInformationalAlert(String title, String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}