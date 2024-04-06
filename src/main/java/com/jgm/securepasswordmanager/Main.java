package com.jgm.securepasswordmanager;

import com.jgm.securepasswordmanager.services.UserDataService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @Override
    public void init() {
        try {
            // Configure the logger with handler and formatter
            FileHandler fileHandler = new FileHandler("data/logs/securepasswordmanager%u.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

            UserDataService theUserDataService = new UserDataService();
            if (!theUserDataService.createAllProgramDirectories()) {
                logger.severe("Failed to create program directories.");
                // Since directories are critical, throw a RuntimeException here
                throw new RuntimeException("Failed to create program directories. The application will terminate.");
            } else {
                logger.info("Program directories created successfully.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while setting up logging", e);
            throw new RuntimeException("Error occurred while setting up logging. The application will terminate.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting application");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 375, 427);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
