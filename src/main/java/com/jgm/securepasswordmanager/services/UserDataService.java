// UserDataService handles the storage, retrieval, and encryption of user data for the Secure Password Manager application.
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

    // Gson instance for converting objects to JSON and vice versa. Pretty printing is enabled for readability.
    private static final Gson theGson = new GsonBuilder().setPrettyPrinting().create();

    // The default directory path for storing user data. Can be overridden.
    private static String theUserDataDirectoryPath = DirectoryPath.USERS_DIRECTORY;

    // Allows setting a custom directory path for user data storage.
    public static void setTheUserDataDirectoryPath(String theUserDataDirectoryPath) {
        UserDataService.theUserDataDirectoryPath = theUserDataDirectoryPath;
    }

    /**
     * Creates a directory if it does not already exist.
     * @param directoryName The name of the directory to create.
     * @return true if the directory was created or already exists, false otherwise.
     */
    public static boolean createDirectoryIfNotExists(String directoryName) {
        File directory = new File(System.getProperty("user.dir"), directoryName);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    // Helper method to ensure the user data directory exists.
    private static boolean createUserDataDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_DIRECTORY);
    }

    // Helper method to ensure the master key directory exists.
    private static boolean createUserMasterKeyDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.USERS_MASTER_KEY);
    }

    // Helper method to ensure the QR code directory exists.
    private static boolean createQRCodeDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.QR_CODE_DIRECTORY);
    }

    // Helper method to ensure the logs directory exists.
    private static boolean createLogsDirectory() {
        return createDirectoryIfNotExists(DirectoryPath.LOGS_DIRECTORY);
    }

    /**
     * Ensures all necessary directories for the application are created.
     * @return true if all directories are successfully created or exist, false otherwise.
     */
    public static boolean createAllProgramDirectories() {
        return createUserDataDirectory() && createUserMasterKeyDirectory() && createQRCodeDirectory() && createLogsDirectory();
    }

    /**
     * Writes a user object to a file in JSON format.
     * @param user The user object to serialize and write.
     * @return true if the user was successfully written to the file, false otherwise.
     */
    public static boolean writeUserToFile(User user) {
        String userDataFilename = theUserDataDirectoryPath + File.separator + user.getUserName() + ".json";

        // Converts ObservableList to List for Gson serialization
        List<WebsiteCredential> credentialList = new ArrayList<>(user.getWebsiteCredentialObservablelList());
        user.setWebsiteCredentialNormalList(credentialList); // Set the normal list for serialization

        encryptUserAccountPassword(user); // Encrypt the user's account password

        if (user.getHasCreatedMasterPassword()) {
            encryptUserWebsites(user); // Encrypt website credentials if a master password is set
        }

        try (Writer writer = new FileWriter(userDataFilename)) {
            // Serialize the user object to JSON and write it to the file
            theGson.toJson(user, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads user objects from JSON files.
     * @return A list of User objects deserialized from the JSON files.
     */
    public static List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        File directory = new File(theUserDataDirectoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    try (Reader reader = new FileReader(file)) {
                        User user = theGson.fromJson(reader, User.class);

                        decryptUserAccountPassword(user); // Decrypt the user's account password

                        if (user.getHasCreatedMasterPassword()) {
                            decryptUserWebsites(user); // Decrypt website credentials if a master password is set
                        }
                        // Convert the list back to ObservableList after deserialization
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

    // Encrypts the user's account password using a predefined secret key and salt.
    private static void encryptUserAccountPassword(User user) {
        user.setPassword(EncryptionService.encrypt(user.getPassword(),"secretkey", "somerandomsalt"));
    }

    // Encrypts passwords for all websites associated with the user using the user's master password.
    private static void encryptUserWebsites(User user) {
        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialObservablelList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(EncryptionService.encrypt(webSiteCred.getWebSitePassword(),
                    user.getMasterPassword(), "somerandomsalt"));
        }
    }

    // Decrypts the user's account password using a predefined secret key and salt.
    private static void decryptUserAccountPassword(User user) {
        String theUsersDecryptedPassword = EncryptionService.decrypt(user.getPassword(), "secretkey", "somerandomsalt");
        user.setPassword(theUsersDecryptedPassword);
    }

    // Decrypts passwords for all websites associated with the user using the user's master password.
    private static void decryptUserWebsites(User user) {
        List<WebsiteCredential> theUsersWebSiteCreds = user.getWebsiteCredentialNormalList();

        for (WebsiteCredential webSiteCred : theUsersWebSiteCreds) {
            webSiteCred.setWebSitePassword(EncryptionService.decrypt(webSiteCred.getWebSitePassword(),
                    user.getMasterPassword(), "somerandomsalt"));
        }
    }
}
