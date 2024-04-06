package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PasswordManagerController {

    @FXML
    private TableView<WebsiteCredential> tableView;

    @FXML
    private TableColumn<WebsiteCredential, String> firstNameCol;

    @FXML
    private TableColumn<WebsiteCredential, String> lastNameCol;

    @FXML
    private TableColumn<WebsiteCredential, String> phoneNumberCol;

    @FXML
    private TableColumn<WebsiteCredential, String> notesCol;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label emailLabel;

    private User theLoadedUser;

    private AuthenticationService theAuthenticationService;

    @FXML
    private TableColumn<WebsiteCredential, String> passwordColumn;

    @FXML
    private VBox centerVBox;




    public void initialize() {
        theAuthenticationService = new AuthenticationService();

        createContextMenu();
        tableView.setPlaceholder(new Label("Your Secure Password Vault is Currently Empty."));

        // Configure the password column's cell factory
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
                        System.out.println("Password for " + credential.getWebSiteName() + ": " + displayedPassword); // Debug line
                        setText(displayedPassword);
                    } else {
                        setText(null);
                    }
                }
            }
        });

        // No need to add the passwordColumn again, it's already defined in the FXML
        // tableView.getColumns().add(passwordColumn); // REMOVE this line
    }

        // Make sure to add this column to your tableView if not already added
//        tableView.getColumns().add(passwordColumn);



    // Used by the LoginController class to pass in the loaded user
    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot set a null User.");
        }
        this.theLoadedUser = user;

        //Bind the TableView to the list of websitecredentials allowing for the tableview display of all website creds
        tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
        // If you need to initialize components with user data, call a method to do it here
        userNameLabel.setText(theLoadedUser.getUserName());
        emailLabel.setText(theLoadedUser.getEmailAddress());

    }


    @FXML
    public void onLogsClicked() {
        loadController("/com/jgm/securepasswordmanager/logs.fxml");
    }




    private void createContextMenu() {
        listContextMenu = new ContextMenu();

        MenuItem togglePasswordVisibilityMenuItem = new MenuItem("Show/Hide Password");
        togglePasswordVisibilityMenuItem.setOnAction(actionEvent -> handleTogglePasswordVisibility());


        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> handleDeleteAction());


        listContextMenu.getItems().addAll(togglePasswordVisibilityMenuItem, deleteMenuItem);

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


    private void handleDeleteAction() {
        WebsiteCredential websiteToDelete = tableView.getSelectionModel().getSelectedItem();
        if (websiteToDelete != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Website Credential");

            // Set the header text with the details of the credential to be deleted
            alert.setHeaderText("Are you sure you want to delete the selected credentials: \n\n" +
                    "Website: " + websiteToDelete.getWebSiteName() + "\n" +
                    "User Name: " + websiteToDelete.getWebSiteUserName());
            alert.setContentText("Are you sure?\n");

            // Show the alert and wait for the user's response
            Optional<ButtonType> result = alert.showAndWait();

            // If the OK button is clicked, remove the credential from the user's list
            if (result.isPresent() && result.get() == ButtonType.OK) {
                theLoadedUser.removeCredential(websiteToDelete);
                // Assuming AuthenticationService is a service you've implemented for managing users
                theAuthenticationService.saveUser(theLoadedUser);
                theLoadedUser = theAuthenticationService.login(theLoadedUser.getUserName(), theLoadedUser.getPassword());

                // Update the TableView to display the list of credentials excluding the deleted one
                tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
                tableView.refresh();
            }
        }
    }

    private void handleTogglePasswordVisibility() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if (selectedCredential != null) {
            selectedCredential.setPasswordVisible(!selectedCredential.isPasswordVisible());
            tableView.refresh(); // This will trigger an update to the TableView
        }
    }


    @FXML
    public void onLogOutButtonClicked(ActionEvent event) {
        loadController(event, "/com/jgm/securepasswordmanager/login.fxml");
    }

    private void loadController(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setX(800);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Directly use the main stage from the current scene without needing an event
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setX(800); // Set position if necessary
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
            // TODO change this to theAuthenticationService.save() once AuthenticationService is available.

            // TODO put this next few lines of code in its own method
            // as this ensures that after the chagnes are mae the encrypted password
            // doesnt show up in the tableview this same few lines of coe are in the
            // rightlcikeddletemethod code too

            String userName = theLoadedUser.getUserName();
            String password = theLoadedUser.getPassword();

            theAuthenticationService.saveUser(theLoadedUser);

            theLoadedUser = theAuthenticationService.login(userName, password);


            // Update the TableView to display the list of credentials including the new one
            tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
            tableView.refresh();

            // Select the new credential in the TableView to highlight it for the user
            int newIndex = theLoadedUser.getWebsiteCredentialObservablelList().indexOf(newItem);
            tableView.getSelectionModel().select(newIndex);
        }
    }

    @FXML
    public void onEditWebsiteCredentialClicked() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if(selectedCredential != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.setTitle("Edit Website Credential");
            dialog.setHeaderText("Edit your Credential");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/com/jgm/securepasswordmanager/add_password.fxml"));

            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
                return;
            }

            AddPasswordController controller = fxmlLoader.getController();
            controller.prepopulateFields(selectedCredential);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                // if this doesnt work do this
//                theLoadedUser.addCredential(controller.updateWebsiteCredential(selectedCredential));

                WebsiteCredential newCredential = controller.processResults();

                selectedCredential.setWebSiteName(newCredential.getWebSiteName());
                selectedCredential.setWebSitePassword(newCredential.getWebSitePassword());
                selectedCredential.setWebSiteUserName(newCredential.getWebSiteUserName());
                selectedCredential.setNotes(newCredential.getNotes());

                String userName = theLoadedUser.getUserName();
                String password = theLoadedUser.getPassword();

                theAuthenticationService.saveUser(theLoadedUser); // Save updated credentials
                theLoadedUser = theAuthenticationService.login(userName, password);

                tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());
                tableView.refresh(); // Refresh the TableView to show the updated data
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Credential Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the website credential you want to edit.");
            alert.showAndWait();
        }
    }


    public void pauseAndLoadMasterPasswordController(String fxmlPath, User loadedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            MasterPasswordController masterPasswordController = loader.getController();
            masterPasswordController.setUser(loadedUser);

            // Creating a new Stage for the Master Password window
            Stage masterPasswordStage = new Stage();
            masterPasswordStage.setTitle("Create Master Password");

            // Assuming you have a reference to the current window's stage:
            Stage primaryStage = (Stage) mainBorderPane.getScene().getWindow();
            masterPasswordStage.initModality(Modality.APPLICATION_MODAL);
            masterPasswordStage.initOwner(primaryStage);

            Scene scene = new Scene(root);
            masterPasswordStage.setScene(scene);

            // Set onCloseRequest event handler to load login controller
            masterPasswordStage.setOnCloseRequest(windowEvent -> {
                loadController("/com/jgm/securepasswordmanager/login.fxml");
            });

            // Show the modal window and wait
            masterPasswordStage.showAndWait();

            // After window closes, access the updated user from the controller
            this.theLoadedUser = masterPasswordController.getUser();

            // Here, you can now use theUpdatedUser as needed
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void onDeleteWebsiteCredential() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if(selectedCredential == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Credential Selected");
            alert.setHeaderText(null);
            alert.setContentText("PLease select the website credential you want to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Credential");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected credentials: \n" +
                "Website: " + selectedCredential.getWebSiteName() + "\n" +
                "User Name: " + selectedCredential.getWebSiteUserName());

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            theLoadedUser.removeCredential(selectedCredential);

            // TODO Call Delete website credential in data services here once created
            // TODO Or just overwrite the file which would be easier


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
//        theUserDataService.writeUserToFile(theLoadedUser);

//        List<User> theLoadedUserList = theUserDataService.loadUsersFromFile();
//        theLoadedUser = theLoadedUserList.get(0);
//
//        // TODO REMOVE THIS!!
//        theLoadedUser = theAuthenticationService.login("jbutler86", "secretpassword");

    }


}