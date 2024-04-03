package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDataService {
//    private final String USERS_DIRECTORY_PATH = "data/user_data";
//    private final String USERS_MASTER_KEY_PATH = "data/user_master_keys";
//    private final String QR_CODE_PATH = "data/qr_codes";
    private EncryptionService theEncryptionService;
    private Gson theGson;

    public UserDataService() {
        this.theEncryptionService = new EncryptionService();
        this.theGson = new GsonBuilder().setPrettyPrinting().create();
    }

//    // Creates the user_data directory if it doesn't already exist
//    public boolean createUserDataDirectory() {
//
//        // Obtain the path to the users current working directory / where the program was executed from.
//        String currentPath = System.getProperty("user.dir");
//
//        // Creates user_data directory path by concating current path
//        // with OS specific separator (e.g. / or \) and the user data directory
//        File userDataDirectory = new File(currentPath + File.separator + USERS_DIRECTORY_PATH);
//
//        if (!userDataDirectory.exists()) {
//            try {
//                boolean wasCreated = userDataDirectory.mkdirs();
//                if (!wasCreated) {
//                    return false;
//                }
//            } catch (SecurityException e) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    /** Creates a directory if it does not exist already. */
    private static boolean createDirectoryIfNotExists(String directoryName) {
        File directory = new File(System.getProperty("user.dir"), directoryName);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    private boolean createUserDataDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_DIRECTORY);
    }

    private boolean createUserMasterKeyDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_MASTER_KEY);
    }

    private boolean createQRCodeDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.QR_CODE_DIRECTORY);
    }



    public boolean createAllProgramDirectories() {
        return createUserDataDirectory() && createUserMasterKeyDirectory() && createQRCodeDirectory();
    }

    public boolean writeUserToFile(User user) {

        String userDataFilename = DirectoryPath.USERS_DIRECTORY + File.separator + user.getUserName() + ".json";

        // Convert ObservableList to List for Gson serialization
        List<WebsiteCredential> credentialList = new ArrayList<>(user.getWebsiteCredentialObservablelList());
        user.setWebsiteCredentialNormalList(credentialList); // Set the normal list for serialization

        encryptUserData(user); // Encrypt after setting the websiteCredentialNormalList

        try (Writer writer = new FileWriter(userDataFilename)) {
            // Serialize the user with the normal list
            theGson.toJson(user, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        File directory = new File(DirectoryPath.USERS_DIRECTORY);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    try (Reader reader = new FileReader(file)) {
                        User user = theGson.fromJson(reader, User.class);

                        decryptUserData(user); // Decrypt before setting the observable list

                        // Convert List back to ObservableList after deserialization
                        ObservableList<WebsiteCredential> observableList =
                                FXCollections.observableArrayList(user.getWebsiteCredentialNormalList());
                        user.setWebsiteCredentialObservablelList(observableList); // Set the observable list

                        users.add(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return users;
    }

//    public ObservableList<User> loadUsersFromFile() {
//        ObservableList<User> users = FXCollections.observableArrayList();
//        File directory = new File(USERS_DIRECTORY_PATH);
//
//        if (directory.exists() && directory.isDirectory()) {
//            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
//
//            if (files != null) {
//                for (File file : files) {
//                    try (Reader reader = new FileReader(file)) {
//                        User user = theGson.fromJson(reader, User.class);
//
//                        decryptUserData(user); // Decrypt before setting the observable list
//
//                        // Convert List back to ObservableList after deserialization
//                        ObservableList<WebsiteCredential> observableList =
//                                FXCollections.observableArrayList(user.getWebsiteCredentialNormalList());
//                        user.setWebsiteCredentialObservablelList(observableList); // Set the observable list
//
//                        users.add(user);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return users;
//    }


    private void encryptUserData(User user) {
        user.setPassword(theEncryptionService.encrypt(user.getPassword(), "thesecretkey", "somerandomsalt"));
        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialObservablelList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(theEncryptionService.encrypt(webSiteCred.getWebSitePassword(),
                    "thesecretkey", "somerandomsalt"));
        }
    }

    private void decryptUserData(User user) {
        String theUsersDecryptedPassword = theEncryptionService.decrypt(user.getPassword(), "thesecretkey", "somerandomsalt");
        user.setPassword(theUsersDecryptedPassword);
        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialNormalList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(theEncryptionService.decrypt(webSiteCred.getWebSitePassword(),
                    "thesecretkey", "somerandomsalt"));
        }
    }

}






