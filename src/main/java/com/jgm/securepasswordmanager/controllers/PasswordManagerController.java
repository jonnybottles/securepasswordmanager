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

    private User theLoadedUser;

    private UserDataService theUserDataService;


    public void initialize() {
        theUserDataService = new UserDataService();

        loadTestUserAndWebsites();

        createDeleteContextMenu();

        //Bind the TableView to the list of websitecredentials
        tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());

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
            rightClickDeleteWebsiteCredential(websiteToDelete);
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

    // Provides right click delete functionality
    public void rightClickDeleteWebsiteCredential(WebsiteCredential websiteToDelete) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Website Credential");
        alert.setHeaderText("Are you sure you want to delete the selected credentials: \n\n" +
                "Website: " + websiteToDelete.getWebSiteName() + "\n" +
                "User Name: " + websiteToDelete.getWebSiteUserName());
        alert.setContentText("Are you sure?\n");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            theLoadedUser.removeCredential(websiteToDelete);
        }
    }

    @FXML
    public void onAddWebsiteCredentialClicked() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Website Credential");
        dialog.setHeaderText("Please enter in the information for your new credentials.");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/jgm/securepasswordmanager/add_password.fxml"));
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
//            addPasswordController.setUser(theLoadedUser);
            WebsiteCredential newItem = addPasswordController.processResults();

            theLoadedUser.addCredential(newItem);
            //Bind the TableView to the list of website credentials
            tableView.setItems(theLoadedUser.getWebsiteCredentialObservablelList());

            //Find the index of the new website credential
            int newIndex = theLoadedUser.getWebsiteCredentialObservablelList().indexOf(newItem);
            tableView.getSelectionModel().select(newIndex);

        }

    }


    @FXML
    public void onEditWebsiteCredentialClicked() {
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
            // TODO Call Edit website in userdata services here once created
            // or just overwrite the file which would be easier
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

    public void loadTestUserAndWebsites() {
        // Create credentials

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

    }


}