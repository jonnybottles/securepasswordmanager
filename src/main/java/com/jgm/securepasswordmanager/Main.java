package com.jgm.securepasswordmanager;

import com.jgm.securepasswordmanager.services.UserDataService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void init() {
        UserDataService theUserDataService = new UserDataService();
        if (!theUserDataService.createAllProgramDirectories()) {
            System.out.println("Failed to create program directories.");
            // Since directories are critical, throw a RuntimeException here
            throw new RuntimeException("Failed to create program directories. The application will terminate.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
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
