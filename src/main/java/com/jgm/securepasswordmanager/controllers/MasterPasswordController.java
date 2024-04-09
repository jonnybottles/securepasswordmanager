package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
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

public class MasterPasswordController {
	@FXML
	private PasswordField masterPasswordField;

	@FXML
	private PasswordField confirmMasterPasswordField;

	@FXML
	private Label validatePasswordLabel;

	private User theLoadedUser;

	private AuthenticationService theAuthenticationService;

	@FXML
	private void initialize() {
		theAuthenticationService = new AuthenticationService();

	}




	// Method to call when OK button is clicked, to check if passwords match
	@FXML
	public void handleOKButtonClicked(ActionEvent event) {
		String masterPassword = masterPasswordField.getText().trim();
		String confirmMasterPassword = confirmMasterPasswordField.getText().trim();

		if (!masterPassword.isEmpty() && masterPassword.equals(confirmMasterPassword)) {
			validatePasswordLabel.setText(""); // Clear any previous error message
			theLoadedUser.setMasterPassword(masterPassword);
			theLoadedUser.setHasCreatedMasterPassword(true);
			// Close the current stage (window)

			String userName = theLoadedUser.getUserName();
			String password = theLoadedUser.getPassword();

			theAuthenticationService.saveUser(theLoadedUser);


			theLoadedUser = theAuthenticationService.login(userName, password);

			closeCurrentStage(event);

		} else {
			validatePasswordLabel.setText("Passwords do not match.");
			validatePasswordLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
		}
	}

	@FXML
	public void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/login.fxml");
	}

	private void loadController(ActionEvent event, String fxmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
//			stage.setX(800);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeCurrentStage(ActionEvent event) {
		// Get the current window (stage) and close it
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}


	// Used by the LoginController class to pass in the loaded user
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;
	}

	public User getUser() {
		return this.theLoadedUser;
	}
}
