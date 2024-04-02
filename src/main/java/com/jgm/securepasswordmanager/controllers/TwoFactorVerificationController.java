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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TwoFactorVerificationController {

	@FXML
	private Label OTPVerificationLabel;

	@FXML
	private TextField OTPCodeField;

	private User theLoadedUser;

	private AuthenticationService theAuthenticationService;

	public void initialize() {
		theAuthenticationService = new AuthenticationService();
	}

	@FXML
	private void handleVerifyButtonClicked(ActionEvent event) {
		String authenticationCode = OTPCodeField.getText().trim();
		if (authenticationCode.equals("goodcode")) {
			OTPVerificationLabel.setText("    Login successful.\n     Loading your secure password vault...");
			OTPVerificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			pauseAndLoadPasswordManagerController(event, "/com/jgm/securepasswordmanager/password_manager.fxml", 4, theLoadedUser);
		} else {
			OTPVerificationLabel.setText("Invalid authentication code.\n Please wait for the next code and try again...");
			OTPVerificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
		}

	}

	@FXML
	private void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login");
	}


	// Used by the LoginController class to pass in the loaded user
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;

	}


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

	private void pauseAndLoadPasswordManagerController(ActionEvent event, String fxmlPath, double pauseSeconds, User theLoadedUser) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
				Parent root = loader.load();

				PasswordManagerController passwordManagerController = loader.getController();
				passwordManagerController.setUser(theLoadedUser);

				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				Scene scene = new Scene(root);
				stage.setTitle("Secure Password Vault");
				stage.setScene(scene);

				// Adjust the window position as needed
				stage.setX(400); // Set this value as needed to position the window
				stage.show();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		pause.play();
	}

	private void pauseAndLoadController(ActionEvent event, String fxmlPath, String title, double pauseSeconds) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> loadController(event, fxmlPath, title));
		pause.play();
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
