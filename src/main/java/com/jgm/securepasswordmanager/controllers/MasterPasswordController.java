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
import javafx.stage.Stage;

public class MasterPasswordController {
	// Fields for user input of master password and its confirmation
	@FXML
	private PasswordField masterPasswordField;
	@FXML
	private PasswordField confirmMasterPasswordField;

	// Label for displaying password validation errors
	@FXML
	private Label validatePasswordLabel;

	// User instance for the currently loaded user
	private User theLoadedUser;

	// Authentication service instance for user authentication tasks
	private AuthenticationService theAuthenticationService;

	// Initializes the controller, primarily setting up the AuthenticationService instance
	@FXML
	private void initialize() {
		theAuthenticationService = new AuthenticationService();
	}

	// Handles the OK button click event to set the master password
	@FXML
	public void handleOKButtonClicked(ActionEvent event) {
		// Retrieve and trim passwords from input fields
		String masterPassword = masterPasswordField.getText().trim();
		String confirmMasterPassword = confirmMasterPasswordField.getText().trim();

		// Check if the master password is not empty and matches its confirmation
		if (!masterPassword.isEmpty() && masterPassword.equals(confirmMasterPassword)) {
			validatePasswordLabel.setText(""); // Clear previous error message
			theLoadedUser.setMasterPassword(masterPassword); // Set the master password for the loaded user
			theLoadedUser.setHasCreatedMasterPassword(true); // Mark that the user has created a master password

			// Save the user with the newly set master password
			String userName = theLoadedUser.getUserName();
			String password = theLoadedUser.getPassword();
			theAuthenticationService.saveUser(theLoadedUser);
			theLoadedUser = theAuthenticationService.login(userName, password);

			// Close the current stage (window)
			closeCurrentStage(event);
		} else {
			// Display error if passwords do not match
			validatePasswordLabel.setText("Passwords do not match.");
			validatePasswordLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
		}
	}

	// Handles the cancel button click event, navigating back to the login screen
	@FXML
	public void handleCancelButtonClicked(ActionEvent event) {
		loadController(event, "/com/jgm/securepasswordmanager/login.fxml");
	}

	// Utility method for loading a different scene based on the specified FXML file path
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

	// Closes the current stage (window) from which the event was triggered
	private void closeCurrentStage(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	// Allows the LoginController class to pass the loaded user into this controller
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;
	}

	// Returns the currently loaded user
	public User getUser() {
		return this.theLoadedUser;
	}
}
