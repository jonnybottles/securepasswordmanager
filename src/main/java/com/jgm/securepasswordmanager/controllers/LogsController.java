package com.jgm.securepasswordmanager.controllers;

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

import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogsController {

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


	private User theLoadedUser;

	@FXML
	public void initialize() {
		setupColumns();
		loadLogs();
	}

	private void setupColumns() {
		dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
		timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));

		typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeverity()));
		messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
	}

	private void loadLogs() {
		List<LogEntry> logEntries = LogParserService.loadLogs();
		tableView.setItems(FXCollections.observableArrayList(logEntries));
	}

	// Used by the LoginController class to pass in the loaded user
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("Cannot set a null User.");
		}
		this.theLoadedUser = user;

		userNameLabel.setText(theLoadedUser.getUserName());
		emailLabel.setText(theLoadedUser.getEmailAddress());
	}

	@FXML
	public void onReturnToPasswordVaultClicked(ActionEvent event) {
		pauseAndLoadPasswordManagerController(event, "/com/jgm/securepasswordmanager/password_manager.fxml", 0, theLoadedUser);
	}

	private void pauseAndLoadPasswordManagerController(ActionEvent event, String fxmlPath, double pauseSeconds, User theLoadedUser) {
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
		pause.setOnFinished(e -> {
			Platform.runLater(() -> { // Schedule this to run later
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
					Parent root = loader.load();

					PasswordManagerController passwordManagerController = loader.getController();
					passwordManagerController.setUser(theLoadedUser);

					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					Scene scene = new Scene(root);
					stage.setTitle("Secure Password Vault");
					stage.setScene(scene);

					stage.setX(400); // Adjust as needed
					stage.show();

					if (!theLoadedUser.getHasCreatedMasterPassword()) {
						passwordManagerController.pauseAndLoadMasterPasswordController("/com/jgm/securepasswordmanager/master_password.fxml",  theLoadedUser);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		});
		pause.play();
	}


	@FXML
	public void onLogOutButtonClicked(ActionEvent event) {
		loadController("/com/jgm/securepasswordmanager/login.fxml", event);
	}

	private void loadController(String fxmlPath, ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();
			Stage stage = (Stage) ((event.getSource() instanceof Stage) ? event.getSource() : mainBorderPane.getScene().getWindow());
			stage.setScene(new Scene(root));
			stage.setX(800);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			// Consider logging this exception or showing a user-friendly error message
		}
	}
}
