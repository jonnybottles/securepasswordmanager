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

    private static final Gson theGson = new GsonBuilder().setPrettyPrinting().create();;

    private static String theUserDataDirectoryPath = DirectoryPath.USERS_DIRECTORY;

    public static void setTheUserDataDirectoryPath(String theUserDataDirectoryPath) {
        UserDataService.theUserDataDirectoryPath = theUserDataDirectoryPath;
    }

    /** Creates a directory if it does not exist already. */
    public static boolean createDirectoryIfNotExists(String directoryName) {
        File directory = new File(System.getProperty("user.dir"), directoryName);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    private static boolean createUserDataDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_DIRECTORY);
    }

    private static boolean createUserMasterKeyDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_MASTER_KEY);
    }

    private static boolean createQRCodeDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.QR_CODE_DIRECTORY);
    }

    private static boolean createLogsDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.LOGS_DIRECTORY);
    }



    public static boolean createAllProgramDirectories() {
        return createUserDataDirectory() && createUserMasterKeyDirectory() && createQRCodeDirectory() && createLogsDirectory();
    }

    public static boolean writeUserToFile(User user) {

        String userDataFilename = theUserDataDirectoryPath + File.separator + user.getUserName() + ".json";

        // Convert ObservableList to List for Gson serialization
        List<WebsiteCredential> credentialList = new ArrayList<>(user.getWebsiteCredentialObservablelList());
        user.setWebsiteCredentialNormalList(credentialList); // Set the normal list for serialization

        encryptUserAccountPassword(user);

        if (user.getHasCreatedMasterPassword()) {
            encryptUserWebsites(user); // Encrypt after setting the websiteCredentialNormalList
        }



        try (Writer writer = new FileWriter(userDataFilename)) {
            // Serialize the user with the normal list
            theGson.toJson(user, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        File directory = new File(theUserDataDirectoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    try (Reader reader = new FileReader(file)) {
                        User user = theGson.fromJson(reader, User.class);

                        decryptUserAccountPassword(user);

                        if (user.getHasCreatedMasterPassword()) {
                            decryptUserWebsites(user); // Encrypt after setting the websiteCredentialNormalList
                        }
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


    private static void encryptUserAccountPassword(User user) {
        user.setPassword(EncryptionService.encrypt(user.getPassword(),"secretkey", "somerandomsalt"));

    }

    private static void encryptUserWebsites(User user) {
        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialObservablelList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(EncryptionService.encrypt(webSiteCred.getWebSitePassword(),
                    user.getMasterPassword(), "somerandomsalt"));
        }
    }

    private static void decryptUserAccountPassword(User user) {
        String theUsersDecryptedPassword = EncryptionService.decrypt(user.getPassword(), "secretkey", "somerandomsalt");
        user.setPassword(theUsersDecryptedPassword);
    }

    private static void decryptUserWebsites(User user) {

        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialNormalList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(EncryptionService.decrypt(webSiteCred.getWebSitePassword(),
                    user.getMasterPassword(), "somerandomsalt"));
        }
    }

}






