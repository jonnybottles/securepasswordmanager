package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataServiceTest {

    private UserDataService theUserDataService;
    private final File userDataDirectory = new File("user_data");

    @BeforeEach
    public void setUp() {
        // Initialize UserDataService for each test
        theUserDataService = new UserDataService();
        // Ensure the test directory is clean before each test
        recursiveDelete(userDataDirectory.toPath());
    }

    @AfterEach
    public void tearDown() {
        // Clean up after each test by deleting the test directory and its contents
        recursiveDelete(userDataDirectory.toPath());
    }

    @Test
    public void testCreateUserDataDirectory() {
        // Verify the directory does not already exist
        assertFalse(userDataDirectory.exists());

        // Attempt to create the directory and verify success
        boolean result = theUserDataService.createUserDataDirectory();

        // The directory should now exist
        assertTrue(result);
        assertTrue(userDataDirectory.exists());
    }

    @Test
    public void testWriteUserToFile() {
        // Prepare user data for writing to file
        User theUser = prepareUserData();

        // Attempt to write user to file and verify success
        assertTrue(theUserDataService.writeUserToFile(theUser));

        // Verify that the file now exists
        File userFile = new File(userDataDirectory, theUser.getUserName() + ".json");
        assertTrue(userFile.exists());
    }

    @Test
    public void testLoadUsersFromFile() {
        // Prepare and write a user to file for testing load functionality
        User theUser = prepareUserData();
        assertTrue(theUserDataService.writeUserToFile(theUser));

        // Load users from file and verify that the list is not empty
        ObservableList<User> users = theUserDataService.loadUsersFromFile();
        assertFalse(users.isEmpty());

        // Verify that loaded user details match those of the written user
        User loadedUser = users.get(0);
        assertEquals(theUser.getUserName(), loadedUser.getUserName());
    }

    @Test
    public void testEncryptionAndDecryptionOfPasswords() {
        // Prepare and write a user to file for testing load functionality
        User theUser = prepareUserData(); // Prepare user data for testing

        String clearTextUserPassword = theUser.getPassword(); // Get the clear text password

        List<String> clearTextWebSitePasswords = populatePasswordsList(theUser); // Get clear text website passwords

        // Print user information before writing to file
        System.out.println("User information before writing to file:");
        System.out.println(theUser);

        assertTrue(theUserDataService.writeUserToFile(theUser)); // Write the user to file

        ObservableList<User> theLoadedUsers = theUserDataService.loadUsersFromFile(); // Load users from file

        // Assert that loaded user matches the original user
        assertEquals(1, theLoadedUsers.size());
        User loadedUser = theLoadedUsers.get(0);
        assertEquals(theUser, loadedUser);

        // Print user information after loading from file
        System.out.println("\nUser information after loading from file:");
        System.out.println(loadedUser);

        // Check if the user's clear text password matches after decryption
        String loadedUserClearTextPassword = loadedUser.getPassword();
        assertEquals(clearTextUserPassword, loadedUserClearTextPassword);

        // Check if the website passwords match after decryption
        List<String> loadedUserWebSitePasswords = populatePasswordsList(loadedUser);
        assertEquals(clearTextWebSitePasswords, loadedUserWebSitePasswords);
    }

    // Helper method to recursively delete a directory and its contents
    private static void recursiveDelete(Path path) {
        if (Files.exists(path)) {
            try {
                // Walk the file tree and delete each path
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Prepares a user with credentials for testing
    private User prepareUserData() {
        // Create credentials
        WebsiteCredential theBankCreds = new WebsiteCredential("jpmorgan.com", "jjbutler2004", "secretpass3", "My bank account is super secret");
        WebsiteCredential theFacebookCreds = new WebsiteCredential("facebook.com", "jjb29572", "facebookiscool88", "I have no friends :(");

        // Create user and add credentials
        User theUser = new User("Jonathan", "Butler", "xxbutler86xx@gmail.com", "jbutler86", "secretpassword");
        theUser.addCredential(theBankCreds);
        theUser.addCredential(theFacebookCreds);

        return theUser;
    }

    private List<String> populatePasswordsList(User user) {
        List<String> theStringPasswordList = new ArrayList<>();
        List<WebsiteCredential> theWebCredList = user.getWebsiteCredentialList();

        for (WebsiteCredential webSiteCred: theWebCredList) {
            theStringPasswordList.add(webSiteCred.getWebSitePassword());
        }

        return theStringPasswordList;

    }
}
