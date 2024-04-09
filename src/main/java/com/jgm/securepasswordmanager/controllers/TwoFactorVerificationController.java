package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import com.jgm.securepasswordmanager.services.LogParserService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TwoFactorVerificationController {

	// FXML-annotated fields for the OTP verification label and code input
	@FXML
	private Label OTPVerificationLabel;
	@FXML
	private TextField OTPCodeField;

	// User instance to hold the loaded user's information
	private User theLoadedUser;

	// Service for handling authentication tasks
	private AuthenticationService theAuthenticationService;

	// Initializes the controller, setting up necessary service objects
	public void initialize() {
		theAuthenticationService = new AuthenticationService();
	}

	// Event handler for the verify button click action
	@FXML
	private void handleVerifyButtonClicked(ActionEvent event) {
		String authenticationCode = OTPCodeField.getText().trim(); // Retrieves and trims the OTP code from the input field
		String secretKey = theLoadedUser.getSecretKeyFor2FABarcode(); // Retrieves the secret key associated with the user for 2FA

		String userName = theLoadedUser.getUserName(); // Retrieves the user's username

		// Attempts to register 2FA using the provided code and secret key
		if (theAuthenticationService.registerTwoFactorAuthentication(authenticationCode, secretKey)) {
			// On success, updates UI and logs the success event
			OTPVerificationLabel.setText("    Login successful.\n     Loading your secure password vault...");
			OTPVerificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			LogParserService.appendLog(new LogEntry("INFO", "Two factor authentication success. User: " + userName));

			// Proceeds to load the password manager controller after a brief pause
			pauseAndLoadPasswordManagerController(event, "/com/jgm/securepasswordmanager/password_manager.fxml", 4, theLoadedUser);
		} else {
			// On failure, updates UI and logs the failure event
			OTPVerificationLabel.setText("Invalid authentication code.\n Please wait for the next code and try again...");
			OTPVerificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			LogParserService.appendLog(new LogEntry("INFO", "Two factor authentication failure. User: " + userName));
		}
	}

	// Event handler for the cancel button click action, which loads the login controller
	@FXML
	private void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login");
	}

	// Sets the loaded user instance to the specified user
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;
	}

	// Utility method to load a controller based on the specified FXML path and title
	@FXML
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

	// Pauses for a specified time before loading the password manager controller, potentially for showing a loading screen or transition
	private void pauseAndLoadPasswordManagerController(ActionEvent event, String fxmlPath, double pauseSeconds, User theLoadedUser) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> {
			Platform.runLater(() -> { // Ensures UI updates are performed on the JavaFX Application Thread
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
					Parent root = loader.load();

					PasswordManagerController passwordManagerController = loader.getController();
					passwordManagerController.setUser(theLoadedUser); // Passes the user information to the next controller

					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					Scene scene = new Scene(root);
					stage.setTitle("Secure Password Vault"); // Sets the title for the next window
					stage.setScene(scene);
					stage.setX(400); // Adjust window position if necessary
					stage.show();

					// If the user hasn't created a master password yet, prompt for its creation
					if (!theLoadedUser.getHasCreatedMasterPassword()) {
						passwordManagerController.pauseAndLoadMasterPasswordController("/com/jgm/securepasswordmanager/master_password.fxml", theLoadedUser);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		});
		pause.play();
	}
}
