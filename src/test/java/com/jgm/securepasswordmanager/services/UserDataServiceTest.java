package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.jgm.securepasswordmanager.utils.FileUtils;
import static org.junit.jupiter.api.Assertions.*;

// UserDataServiceTest is designed to perform unit testing on the UserDataService class,
// specifically focusing on operations such as writing to, reading from files, and encryption/decryption processes.
public class UserDataServiceTest {


    @BeforeEach
    public void setUp() {

        // Ensure the test directory is clean before each test
        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);

        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);

        // Configures a test log file path within the test logs directory.
        LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log"); ;
        UserDataService.setTheUserDataDirectoryPath(DirectoryPath.TEST_USERS_DIRECTORY);

    }

    @AfterEach
    public void tearDown() {
        // Clean up after each test by deleting the test directory and its contents
        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
        FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
    }


    @Test
    public void testWriteUserToFile() {

        // Prepare user data for writing to file
        User theUser = prepareUserData();

        // Attempt to write user to file and verify success
        assertTrue(UserDataService.writeUserToFile(theUser));

        // Verify that the file now exists
        File userFile = new File(DirectoryPath.TEST_USERS_DIRECTORY, theUser.getUserName() + ".json");
        assertTrue(userFile.exists());
    }

    @Test
    public void testLoadUsersFromFile() {
        // Prepare and write a user to file for testing load functionality
        User theUser = prepareUserData();
        assertTrue(UserDataService.writeUserToFile(theUser));

        // Load users from file and verify that the list is not empty
        List<User> users = UserDataService.loadUsersFromFile();
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


        assertTrue(UserDataService.writeUserToFile(theUser)); // Write the user to file

        List<User> theLoadedUsers = UserDataService.loadUsersFromFile(); // Load users from file

        // Assert that loaded user matches the original user
        assertEquals(1, theLoadedUsers.size());
        User loadedUser = theLoadedUsers.get(0);

        // Print user information after loading from file

        // Check if the user's clear text password matches after decryption
        String loadedUserClearTextPassword = loadedUser.getPassword();
        assertEquals(clearTextUserPassword, loadedUserClearTextPassword);

        // Check if the website passwords match after decryption
        List<String> loadedUserWebSitePasswords = populatePasswordsList(loadedUser);
        assertEquals(clearTextWebSitePasswords, loadedUserWebSitePasswords);
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
        ObservableList<WebsiteCredential> theWebCredList = user.getWebsiteCredentialObservablelList();

        for (WebsiteCredential webSiteCred: theWebCredList) {
            theStringPasswordList.add(webSiteCred.getWebSitePassword());
        }

        return theStringPasswordList;

    }

}
