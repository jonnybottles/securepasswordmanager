package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.services.UserDataService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
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

    private User theLoadedUser;

    private UserDataService theUserDataService;


    public void initialize() {
        theUserDataService = new UserDataService();

        setUser();

        createDeleteContextMenu();

        //Bind the TableView to the list of contacts
        tableView.setItems(theLoadedUser.getWebsiteCredentialList());

        // Assuming you have 4 columns, and you want them to take up equal space
//        double width = tableView.widthProperty().divide(4).doubleValue();

//        // Bind the width of each column to a quarter of the table's width
//        firstNameCol.prefWidthProperty().bind(tableView.widthProperty().divide(4));
//        lastNameCol.prefWidthProperty().bind(tableView.widthProperty().divide(4));
//        phoneNumberCol.prefWidthProperty().bind(tableView.widthProperty().divide(4));
//        notesCol.prefWidthProperty().bind(tableView.widthProperty().divide(4));


    }

    private void createDeleteContextMenu() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            WebsiteCredential websiteToDelete = tableView.getSelectionModel().getSelectedItem();
            deleteItem(websiteToDelete);
        });

        listContextMenu.getItems().add(deleteMenuItem); // Add this line

        // Set the row factory for the TableView
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


    // Change this to accept a user when integrating with the other branches
//    public void setUser(User theLoadedUser) {
//        this.theLoadedUser = theLoadedUser;
//    }

    // Remove this after integrating with the other branches
    // this is only being used for testing.
    public void setUser() {
        // Create credentials

        WebsiteCredential cred1 = new WebsiteCredential("google.com", "userGoogle", "password123", "My Google account.");
        WebsiteCredential cred2 = new WebsiteCredential("youtube.com", "userYouTube", "pass456", "My YouTube account.");
        WebsiteCredential cred3 = new WebsiteCredential("amazon.com", "userAmazon", "amz789", "Shopping account.");
        WebsiteCredential cred4 = new WebsiteCredential("twitter.com", "userTwitter", "tweet987", "My Twitter handle.");
        WebsiteCredential cred5 = new WebsiteCredential("linkedin.com", "userLinkedIn", "link321", "Professional profile.");
        WebsiteCredential cred6 = new WebsiteCredential("netflix.com", "userNetflix", "netflixx123", "Binge-watching account.");
        WebsiteCredential cred7 = new WebsiteCredential("spotify.com", "userSpotify", "spoti456", "Music streaming service account.");
        WebsiteCredential cred8 = new WebsiteCredential("jpmorgan.com", "jjbutler2004", "secretpass3", "My bank account is super secret");
        WebsiteCredential cred9 = new WebsiteCredential("facebook.com", "jjb29572", "facebookiscool88", "I have no friends :(");

        // Create user and add credentials
        theLoadedUser = new User("Jonathan", "Butler", "xxbutler86xx@gmail.com", "jbutler86", "secretpassword");
        theLoadedUser.addCredential(cred1);
        theLoadedUser.addCredential(cred2);
        theLoadedUser.addCredential(cred3);
        theLoadedUser.addCredential(cred4);
        theLoadedUser.addCredential(cred5);
        theLoadedUser.addCredential(cred6);
        theLoadedUser.addCredential(cred7);
        theLoadedUser.addCredential(cred8); // Assuming cred8 is theBankCreds
        theLoadedUser.addCredential(cred9); // Assuming cred9 is theFacebookCreds

    }

    // Provides right click delete functionality
    public void deleteItem(WebsiteCredential websiteToDelete) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contact Item");
        alert.setHeaderText("Delete Website: " + websiteToDelete.getWebSiteName() + " " + websiteToDelete.getWebSiteUserName()
        + " credentials?");
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            theLoadedUser.removeCredential(websiteToDelete);
        }
    }

    @FXML
    public void showAddItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Contact");
        dialog.setHeaderText("This is some other instructions");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("add_password.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the add password dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            AddPasswordController addPasswordController = fxmlLoader.getController();
            WebsiteCredential newItem = addPasswordController.processResults();
            //Bind the TableView to the list of contacts
            tableView.setItems(theLoadedUser.getWebsiteCredentialList());

            //Find the index of the new contact
            int newIndex = theLoadedUser.getWebsiteCredentialList().indexOf(newItem);
            tableView.getSelectionModel().select(newIndex);

            System.out.println("OK pressed");
        }

    }


    @FXML
    public void showEditWebsiteCredentialDialog() {
        WebsiteCredential selectedWebsiteCredential = tableView.getSelectionModel().getSelectedItem();
        if(selectedWebsiteCredential == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Website Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the website you want to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Website Credential");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("add_password.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        AddPasswordController theAddPasswordController = fxmlLoader.getController();
        theAddPasswordController.editWebsiteCredential(selectedWebsiteCredential);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            theAddPasswordController.updateWebsiteCredential(selectedWebsiteCredential);
            // TODO Call Edit Contact in data services here once created
            // or just overwrite the file which would be easier
        }
    }

    public void deleteContact() {
        WebsiteCredential selectedCredential = tableView.getSelectionModel().getSelectedItem();
        if(selectedCredential == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contact Selected");
            alert.setHeaderText(null);
            alert.setContentText("PLease select the contact you want to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contact");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected website credential: " +
                selectedCredential.getWebSiteName() + " " + selectedCredential.getWebSiteUserName());

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            theLoadedUser.removeCredential(selectedCredential);

            // TODO Call Delete Contact in data services here once created
            // TODO Or just overwrite the file which would be easier


        }
    }

}