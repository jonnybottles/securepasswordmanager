package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import com.jgm.securepasswordmanager.services.LogParserService;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PasswordManagerController {

    // JavaFX components connected to the FXML layout
    @FXML
    private TableView<WebsiteCredential> tableView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private TableColumn<WebsiteCredential, String> passwordColumn;

    private User theLoadedUser; // Stores the user's information
    private AuthenticationService theAuthenticationService; // Service for handling authentication

    // Method called to initialize the controller
    public void initialize() {
        theAuthenticationService = new AuthenticationService(); // Initializes the authentication service
        createContextMenu(); // Creates a context menu for the table view
        tableView.setPlaceholder(new Label("Your Secure Password Vault is Currently Empty.")); // Sets a placeholder for the empty table

        // Configures the password column to display masked or plain text passwords
        passwordColumn.setCellFactory(column -> new TableCell<WebsiteCredential, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        WebsiteCredential credential = getTableView().getItems().get(index);
                        String displayedPassword = credential.getDisplayedPassword();
                        setText(displayedPassword);
                    } else {
                        setText(null);
                    }
                }
            }
        });
    }

    // Method to set the loaded user in the controller
    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot set a null User.");
        }
        this.theLoadedUser = user;
        // Binds the table view with the user's website credentials
        tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
        userNameLabel.setText(theLoadedUser.getUserName()); // Sets the user's name on the UI
        emailLabel.setText(theLoadedUser.getEmailAddress()); // Sets the user's email on the UI
    }

    // Event handler for when the "Logs" option is clicked
    @FXML
    public void onLogsClicked(ActionEvent event) {
        loadLogsController(event, "/com/jgm/securepasswordmanager/logs.fxml", 0, theLoadedUser);
    }

    // Loads the logs controller after a specified pause
    private void loadLogsController(ActionEvent event, String fxmlPath, double pauseSeconds, User theNewUser) {
        PauseTransition pause = new PauseTransition(Duration.seconds(pauseSeconds));
        pause.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                LogsController theLogsController = loader.getController();
                theLogsController.setUser(theNewUser); // Passes the loaded user to the logs controller

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Secure Password Manager Logs");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    // Creates a context menu for the table view with options for password visibility and deletion
    private void createContextMenu() {
        listContextMenu = new ContextMenu();
        MenuItem togglePasswordVisibilityMenuItem = new MenuItem("Show/Hide Password");
        togglePasswordVisibilityMenuItem.setOnAction(actionEvent -> handleTogglePasswordVisibility());

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> handleDeleteAction());

        listContextMenu.getItems().addAll(togglePasswordVisibilityMenuItem, deleteMenuItem);

        // Binds the context menu to the table row, showing it only when the row is not empty
        tableView.setRowFactory(tv -> {
            TableRow<WebsiteCredential> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(listContextMenu)
            );
            return row;
        });
    }

    // Handles the deletion of a selected credential
    private void handleDeleteAction() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if (selectedCredential == null) {
            // Shows an alert if no credential is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Credential Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the website credential you want to delete.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog for credential deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Credential");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected credentials: \n" +
                "Website: " + selectedCredential.getWebSiteName() + "\n" +
                "User Name: " + selectedCredential.getWebSiteUserName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Logs the deletion of the credential
            LogParserService.appendLog(new LogEntry("INFO", "Website credential deleted successfully." + "User: " + theLoadedUser.getUserName() + " Website: " + selectedCredential.getWebSiteName() + ", Website Username: " + selectedCredential.getWebSiteUserName()));
            theLoadedUser.removeCredential(selectedCredential); // Removes the credential from the user's list

            String userName = theLoadedUser.getUserName();
            String password = theLoadedUser.getPassword();

            /* Save the changes and reload the updated credentials */
            theAuthenticationService.saveUser(theLoadedUser);
            theLoadedUser = theAuthenticationService.login(userName, password);

            // Update the TableView to display the list of credentials including the new one
            tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
            tableView.refresh();
        }
    }

    // Toggles the visibility of the selected credential's password
    private void handleTogglePasswordVisibility() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if (selectedCredential != null) {
            selectedCredential.setPasswordVisible(!selectedCredential.isPasswordVisible()); // Toggles the password visibility
            tableView.refresh(); // Refreshes the table view to update the password display
        }
    }

    // Event handler for the logout button, loads the login controller
    @FXML
    public void onLogOutButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/login.fxml"); // Loads the login view
        LogParserService.appendLog(new LogEntry("INFO", "Log out success. User: " + theLoadedUser.getUserName()));
    }

    // Loads a controller based on the specified FXML path
    private void loadController(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setX(800); // Optionally sets the window position
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Overloaded method to load a controller without needing an event, directly using the main stage
    private void loadController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) mainBorderPane.getScene().getWindow(); // Uses the main stage
            stage.setScene(new Scene(root));
            stage.setX(800); // Sets the window position
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Event handler for adding a new website credential.
    // This loads the AddPasswordController, which opens a dialog for the
    // user to input new credential details
    @FXML
    public void onAddWebsiteCredentialClicked() {
        // Create a new dialog for adding website credentials
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Website Credential");
        dialog.setHeaderText("Please enter in the information for your new credentials.");

        // Set up the FXMLLoader to load the AddPasswordController
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/jgm/securepasswordmanager/add_password.fxml"));

        // Attempt to load the dialog pane with the FXML content
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the add password dialog");
            e.printStackTrace();
            return;
        }

        // Add OK and Cancel buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Display the dialog and wait for the user to close it, capturing the result
        Optional<ButtonType> result = dialog.showAndWait();

        // Handle the user confirming the dialog
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AddPasswordController addPasswordController = fxmlLoader.getController();
            WebsiteCredential newItem = addPasswordController.processResults();

            // Add the new credential to the current user's list
            theLoadedUser.addCredential(newItem);

            String userName = theLoadedUser.getUserName();
            String password = theLoadedUser.getPassword();

            theAuthenticationService.saveUser(theLoadedUser);

            theLoadedUser = theAuthenticationService.login(userName, password);


            // Update the TableView to display the list of credentials including the new one
            tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
            tableView.refresh();

            LogParserService.appendLog(new LogEntry("INFO", "Website credential created successfully." + "User: " + theLoadedUser.getUserName() + " Website: " + newItem.getWebSiteName() + ", Website Username: " + newItem.getWebSiteUserName()));


            // Select the new credential in the TableView to highlight it for the user
            int newIndex = theLoadedUser.getWebsiteCredentialObservablelList().indexOf(newItem);
            tableView.getSelectionModel().select(newIndex);
        }
    }

    /* Method to handle the event when 'Edit Website Credential' is clicked */
    @FXML
    public void onEditWebsiteCredentialClicked() {
        // Get the selected credential from the table.
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        // Check if a credential is selected
        if(selectedCredential != null) {
            /* Create dialog for editing the credential */
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.setTitle("Edit Website Credential");
            dialog.setHeaderText("Edit your Credential");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/com/jgm/securepasswordmanager/add_password.fxml"));

            try {
                /* Set the content for the DialogPane by loading from a fxml file */
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
                return;
            }

            /* Get the AddPasswordController and populate the fields with the selected credentials */
            AddPasswordController controller = fxmlLoader.getController();
            controller.prepopulateFields(selectedCredential);

            /* Add OK and Cancel buttons to the dialog */
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            /* Show the dialog and handle user response */
            Optional<ButtonType> result = dialog.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {

                /* If OK is pressed, process the results and save the updated credentials */
                WebsiteCredential newCredential = controller.processResults();

                /* Update the original credentials */
                selectedCredential.setWebSiteName(newCredential.getWebSiteName());
                selectedCredential.setWebSitePassword(newCredential.getWebSitePassword());
                selectedCredential.setWebSiteUserName(newCredential.getWebSiteUserName());
                selectedCredential.setNotes(newCredential.getNotes());

                String userName = theLoadedUser.getUserName();
                String password = theLoadedUser.getPassword();

                /* Save the credentials and reload the updated credentials */
                theAuthenticationService.saveUser(theLoadedUser);
                theLoadedUser = theAuthenticationService.login(userName, password);

                /* Refresh the table view with the updated credentials */
                tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
                tableView.refresh();

                /* Log the success message */
                LogParserService.appendLog(new LogEntry("INFO", "Website credential edited successfully." + "User: " + theLoadedUser.getUserName() + " Website: " + newCredential.getWebSiteName() + ", Website Username: " + newCredential.getWebSiteUserName()));

            }
        } else {
            /* Show an alert if no website credential is selected */
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Credential Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the website credential you want to edit.");
            alert.showAndWait();
        }
    }

    /* Method to pause and load the master password controller */
    public void pauseAndLoadMasterPasswordController(String fxmlPath, User loadedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            /* Get the MasterPasswordController and set the user */
            MasterPasswordController masterPasswordController = loader.getController();
            masterPasswordController.setUser(loadedUser);

            /* Create a new stage for the master password dialog window */
            Stage masterPasswordStage = new Stage();
            masterPasswordStage.setTitle("Create Master Password");

            Stage primaryStage = (Stage) mainBorderPane.getScene().getWindow();
            masterPasswordStage.initModality(Modality.APPLICATION_MODAL);
            masterPasswordStage.initOwner(primaryStage);

            Scene scene = new Scene(root);
            masterPasswordStage.setScene(scene);

            // Set onCloseRequest event handler to load login controller
            masterPasswordStage.setOnCloseRequest(windowEvent -> {
                loadController("/com/jgm/securepasswordmanager/login.fxml");
            });

            /* Show the stage */
            masterPasswordStage.showAndWait();

            /* After the window closes, get the updated user from the controller */
            this.theLoadedUser = masterPasswordController.getUser();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* Method to handle the event when deleting a website credential */
    public void onDeleteWebsiteCredential() {
        /* Get the selected credential from the table */
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        /* Check if a credential is selected */
        if(selectedCredential == null) {
            /* Show an informational alert if no credential is selected */
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Credential Selected");
            alert.setHeaderText(null);
            alert.setContentText("PLease select the website credential you want to delete.");
            alert.showAndWait();
            return;
        }

        /* Display a confirmation alert before deleting */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Credential");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected credentials: \n" +
                "Website: " + selectedCredential.getWebSiteName() + "\n" +
                "User Name: " + selectedCredential.getWebSiteUserName());

        /* Process the user's response */
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            /* If OK is pressed, remove the credential from the user */
            theLoadedUser.removeCredential(selectedCredential);

            String userName = theLoadedUser.getUserName();
            String password = theLoadedUser.getPassword();

            /* Save the changes and reload the updated credentials */
            theAuthenticationService.saveUser(theLoadedUser);
            theLoadedUser = theAuthenticationService.login(userName, password);

            /* Refresh the table view with the updated credentials */
            tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
            tableView.refresh();
        }
    }

    // Generates test data for tableview
    public void loadTestUserAndWebsites() {
        List<WebsiteCredential> credentialList = new ArrayList<>();

        credentialList.add(new WebsiteCredential("google.com", "userGoogle", "password123", "My Google account."));
        credentialList.add(new WebsiteCredential("youtube.com", "userYouTube", "pass456", "My YouTube account."));
        credentialList.add(new WebsiteCredential("amazon.com", "userAmazon", "amz789", "Shopping account."));
        credentialList.add(new WebsiteCredential("twitter.com", "userTwitter", "tweet987", "My Twitter handle."));
        credentialList.add(new WebsiteCredential("linkedin.com", "userLinkedIn", "link321", "Professional profile."));
        credentialList.add(new WebsiteCredential("netflix.com", "userNetflix", "netflixx123", "Binge-watching account."));
        credentialList.add(new WebsiteCredential("spotify.com", "userSpotify", "spoti456", "Music streaming service account."));
        credentialList.add(new WebsiteCredential("jpmorgan.com", "jjbutler2004", "secretpass3", "My bank account is super secret"));
        credentialList.add(new WebsiteCredential("facebook.com", "jjb29572", "facebookiscool88", "I have no friends :("));
        credentialList.add(new WebsiteCredential("github.com", "octocatDev", "devPass1234", "My development repositories."));
        credentialList.add(new WebsiteCredential("stackoverflow.com", "codeWizard99", "stackOverFlow321", "Where I ask and answer tech questions."));
        credentialList.add(new WebsiteCredential("paypal.com", "paypalUser2024", "securePay123", "My main payment service."));
        credentialList.add(new WebsiteCredential("ebay.com", "auctionMaster", "bidWin2024!", "Where I sell and buy stuff."));
        credentialList.add(new WebsiteCredential("dropbox.com", "cloudStorageMaster", "dropItLikeItsHot$", "For all my important documents."));
        credentialList.add(new WebsiteCredential("twitch.tv", "streamerElite", "watchMeGame123", "My gaming channel."));
        credentialList.add(new WebsiteCredential("airbnb.com", "travelJunkie1986", "wanderlustLife$", "My travel stays."));
        credentialList.add(new WebsiteCredential("pinterest.com", "pinnerCreative", "pinningAway987", "My board of ideas."));
        credentialList.add(new WebsiteCredential("instagram.com", "instaUser_photog", "picturePerfect86", "My photo sharing profile."));
        credentialList.add(new WebsiteCredential("tumblr.com", "bloggerPoet", "wordsOfWisdom!", "Where I express myself."));
        credentialList.add(new WebsiteCredential("microsoft.com", "techUser86", "windowsExpert321", "My software licenses."));
        credentialList.add(new WebsiteCredential("imdb.com", "movieBuff2024", "cinemaLover!", "My movie lists."));
        credentialList.add(new WebsiteCredential("tripadvisor.com", "worldExplorer", "adventureAwaits!", "My travel reviews."));
        credentialList.add(new WebsiteCredential("wikipedia.org", "factFinder", "knowledgeIsPower!", "My edit contributions."));
        credentialList.add(new WebsiteCredential("flickr.com", "shutterbugLife", "snapSnap1234", "My photography portfolio."));
        credentialList.add(new WebsiteCredential("vimeo.com", "videoGrapher", "shootEditUpload!", "My video creations."));
        credentialList.add(new WebsiteCredential("etsy.com", "craftyArtist", "handMadeWithLove", "Where I sell my crafts."));
        credentialList.add(new WebsiteCredential("booking.com", "frequentFlyer", "travelTheWorld123", "Where I plan my trips."));
        credentialList.add(new WebsiteCredential("disneyplus.com", "disneyFanatic", "magicKingdom!", "My streaming for classics."));
        credentialList.add(new WebsiteCredential("hulu.com", "seriesAddict", "streamBinge456", "For all my favorite series."));
        credentialList.add(new WebsiteCredential("expedia.com", "travelDealHunter", "vacationSavings!", "For my travel booking deals."));

        // Create user and add credentials
        theLoadedUser = new User("Jonathan", "Butler", "xxbutler86xx@gmail.com", "jbutler86", "secretpassword");

        for (WebsiteCredential cred: credentialList) {
            theLoadedUser.addCredential(cred);
        }

        theAuthenticationService.saveUser(theLoadedUser);


    }


}