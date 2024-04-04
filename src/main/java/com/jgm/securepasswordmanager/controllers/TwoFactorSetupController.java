package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
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
	private ImageView qrCodeImageView;

	@FXML
	private Label qrCodeVertificationLabel;

	@FXML
	private TextField authenticationCodeField;

	private User theNewUser;
	private AuthenticationService theAuthenticationService;



	public void initialize() {
		// This is where I would pull the users email address, the secret key and then
		// generate the QR code
		theAuthenticationService = new AuthenticationService();
//		generateAndDisplayQRCode();

	}

	public void generateAndDisplayQRCode() {
		String qRCodePath = DirectoryPath.QR_CODE_DIRECTORY + "/" + theNewUser.getUserName() + "_qr_code.png";

		// Generate QR Code Image
		if (!theAuthenticationService.generateQRCode(theNewUser, qRCodePath)) {
			displayErrorAlert("2FA Setup Error", "2FA Setup Error", "Error Generating QR Code.");
		}

		//Load QR Code Image
		File qrImageFile = new File(qRCodePath);
		if (qrImageFile.exists()) {
			Image qrImage = new Image(qrImageFile.toURI().toString());
			qrCodeImageView.setImage(qrImage);
		} else {
			displayErrorAlert("2FA Setup Error", "2FA Setup Error", "QR code image file now found.");
		}


	}

	@FXML
	private void handleVerifyButtonClicked(ActionEvent event) {
		String authenticationCode = authenticationCodeField.getText().trim();

		if (theAuthenticationService.registerTwoFactorAuthentication(authenticationCode, "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK")) {
			theNewUser.setHasRegisteredTwoFactorAuthentication(true);



			String userName = theNewUser.getUserName();
			String password = theNewUser.getPassword();

			theAuthenticationService.saveUser(theNewUser);


			theNewUser = theAuthenticationService.login(userName, password);

			qrCodeVertificationLabel.setText("Two factor authentication setup successfully.\n Returning to login screen...");
			qrCodeVertificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
			pauseAndLoadController(event, "/com/jgm/securepasswordmanager/login.fxml", "Login", 4);
		} else {
			qrCodeVertificationLabel.setText("Invalid authentication code.\n Please wait for the next code and try again...");
			qrCodeVertificationLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");

		}

	}

	@FXML
	private void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/create_new_account.fxml", "New Account");
	}



	// Used by the LoginController class to pass in the loaded user
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theNewUser = user;

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



