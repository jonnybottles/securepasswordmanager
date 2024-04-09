// Controller class for handling the log view in the Secure Password Manager application.
package com.jgm.securepasswordmanager.controllers;

// Import statements for required classes and packages.
import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.LogParserService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class LogsController {

	// FXML-annotated fields corresponding to components in the FXML layout for the logs view.
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private TableView<LogEntry> tableView;
	@FXML
	private TableColumn<LogEntry, String> dateColumn;
	@FXML
	private TableColumn<LogEntry, String> timeColumn;
	@FXML
	private TableColumn<LogEntry, String> typeColumn;
	@FXML
	private TableColumn<LogEntry, String> messageColumn;
	@FXML
	private Label userNameLabel;
	@FXML
	private Label emailLabel;

	// User object to store the information of the currently loaded user.
	private User theLoadedUser;

	// Method called to initialize the controller after the FXML fields are injected.
	@FXML
	public void initialize() {
		setupColumns(); // Configure table columns to display log entries.
		loadLogs(); // Load log entries into the table view.
	}

	// Configures the table columns to display the appropriate fields of LogEntry objects.
	private void setupColumns() {
		dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
		timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
		typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeverity()));
		messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
	}

	// Loads log entries from the log service and displays them in the table view.
	private void loadLogs() {
		List<LogEntry> logEntries = LogParserService.loadLogs();
		tableView.setItems(FXCollections.observableArrayList(logEntries));
	}

	// Method to set the current user. Also updates user-related UI components.
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;
		userNameLabel.setText(theLoadedUser.getUserName()); // Update UI with user's name.
		emailLabel.setText(theLoadedUser.getEmailAddress()); // Update UI with user's email.
	}

	// Event handler for returning to the password vault view.
	@FXML
	public void onReturnToPasswordVaultClicked(ActionEvent event) {
		// Load the password manager controller with a possible pause before showing the UI.
		pauseAndLoadPasswordManagerController(event, "/com/jgm/securepasswordmanager/password_manager.fxml", 0, theLoadedUser);
	}

	// Loads the password manager controller after a pause, if specified.
	private void pauseAndLoadPasswordManagerController(ActionEvent event, String fxmlPath, double pauseSeconds, User theLoadedUser) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> {
			Platform.runLater(() -> { // Ensure UI updates happen on the JavaFX application thread.
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
					Parent root = loader.load();
					PasswordManagerController passwordManagerController = loader.getController();
					passwordManagerController.setUser(theLoadedUser); // Pass the current user to the next controller.
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					Scene scene = new Scene(root);
					stage.setTitle("Secure Password Vault");
					stage.setScene(scene);
					stage.setX(400); // Adjust the window position.
					stage.show();
					// Additional step if the user hasn't created a master password yet.
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

	// Handles the action for the logout button click.
	@FXML
	public void onLogOutButtonClicked(ActionEvent event) {
		// Load the login view.
		loadController("/com/jgm/securepasswordmanager/login.fxml", event);
	}

	// Utility method for loading a controller and switching scenes.
	private void loadController(String fxmlPath, ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();
			// Determine the current stage from the event source or the main border pane.
			Stage stage = (Stage) ((event.getSource() instanceof Stage) ? event.getSource() : mainBorderPane.getScene().getWindow());
			stage.setScene(new Scene(root)); // Set the new scene.
			stage.setX(800); // Adjust the window position.
			stage.show(); // Display the stage.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
