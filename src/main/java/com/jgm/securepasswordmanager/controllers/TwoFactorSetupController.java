// Controller class for the two-factor authentication setup view in the Secure Password Manager application.
package com.jgm.securepasswordmanager.controllers;

// Import statements for necessary classes and packages.
import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import com.jgm.securepasswordmanager.services.LogParserService;
import com.jgm.securepasswordmanager.services.TwoFactorAuthenticationService;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class TwoFactorSetupController {

	@FXML
	private ImageView qrCodeImageView; // ImageView for displaying the QR code.
	@FXML
	private Label qrCodeVertificationLabel; // Label for showing QR code verification status.
	@FXML
	private TextField authenticationCodeField; // TextField for inputting the authentication code.

	private User theNewUser; // Stores user data.
	private AuthenticationService theAuthenticationService; // Handles authentication logic.

	// Initializes the controller and authentication service.
	public void initialize() {
		theAuthenticationService = new AuthenticationService();
	}

	// Generates and displays the QR code for two-factor authentication setup.
	public void generateAndDisplayQRCode() {
		String qRCodePath = DirectoryPath.QR_CODE_DIRECTORY + "/" + theNewUser.getUserName() + "_qr_code.png";

		String secretKey = TwoFactorAuthenticationService.generateSecretKey();
		theNewUser.setSecretKeyFor2FABarcode(secretKey);

		// Generate QR Code Image and handle potential errors.
		if (!theAuthenticationService.generateQRCode(theNewUser, qRCodePath)) {
			displayErrorAlert("2FA Setup Error", "2FA Setup Error", "Error Generating QR Code.");
			theNewUser.setSecretKeyFor2FABarcode(null);
		}

		// Load and display the QR Code Image if it exists.
		File qrImageFile = new File(qRCodePath);
		if (qrImageFile.exists()) {
			Image qrImage = new Image(qrImageFile.toURI().toString());
			qrCodeImageView.setImage(qrImage);
		} else {
			displayErrorAlert("2FA Setup Error", "2FA Setup Error", "QR code image file not found.");
			theNewUser.setSecretKeyFor2FABarcode(null);
		}
	}

	// Handles the action of the Verify button being clicked.
	@FXML
	private void handleVerifyButtonClicked(ActionEvent event) {
		String authenticationCode = authenticationCodeField.getText().trim();
		String userName = theNewUser.getUserName();
		String password = theNewUser.getPassword();

		// Registers two-factor authentication and transitions to the login screen upon success.
		if (theAuthenticationService.registerTwoFactorAuthentication(authenticationCode, theNewUser.getSecretKeyFor2FABarcode())) {
			theNewUser.setHasRegisteredTwoFactorAuthentication(true);
			theAuthenticationService.saveUser(theNewUser);
			theNewUser = theAuthenticationService.login(userName, password);

			qrCodeVertificationLabel.setText("Two factor authentication setup successfully.\n Returning to login screen...");
			qrCodeVertificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			pauseAndLoadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login", 4);
			LogParserService.appendLog(new LogEntry("INFO", "Two factor setup success. User: " + userName));
		} else {
			qrCodeVertificationLabel.setText("Invalid authentication code.\n Please wait for the next code and try again...");
			qrCodeVertificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			LogParserService.appendLog(new LogEntry("INFO", "Two factor setup failure. User: " + userName));
		}
	}

	// Handles the action of the Cancel button being clicked.
	@FXML
	private void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/create_new_account.fxml", "New Account");
	}

	// Sets the user for this controller.
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theNewUser = user;
	}

	// Loads a new scene specified by the fxmlPath.
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

	// Pauses for a specified duration before loading a new controller.
	private void pauseAndLoadController(ActionEvent event, String fxmlPath, String title, double pauseSeconds) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> loadController(event, fxmlPath, title));
		pause.play();
	}

	// Displays an error alert with the provided title, header, and message.
	public void displayErrorAlert(String title, String header, String msg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	// Optionally, could be used to display informational alerts.
	public void displayInformationalAlert(String title, String header, String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
