package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.services.LogParserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
	public void initialize() {
		setupColumns();
		loadLogs();
	}

	private void setupColumns() {
		dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
		timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().severityProperty());
		messageColumn.setCellValueFactory(cellData -> cellData.getValue().messageProperty());
	}

	private void loadLogs() {
		String logFilePath = "data/logs/securepasswordmanager0.log"; // Modify this path as needed
		LogParserService logParserService = new LogParserService();
		List<LogEntry> logEntries;

		try (FileReader fileReader = new FileReader(logFilePath)) {
			logEntries = logParserService.parseLogFile(fileReader);
		} catch (IOException e) {
			logEntries = new ArrayList<>(); // In case of an error, use an empty list
			e.printStackTrace();
		}

		tableView.setItems(FXCollections.observableArrayList(logEntries));
		tableView.refresh();
	}


	@FXML
	public void onReturnToPasswordVaultClicked(ActionEvent event) {
		loadController("/com/jgm/securepasswordmanager/password_manager.fxml", event);
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
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			// Consider logging this exception or showing a user-friendly error message
		}
	}
}
