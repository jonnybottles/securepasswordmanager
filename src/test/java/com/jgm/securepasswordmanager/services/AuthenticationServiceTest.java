package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests authentication services
class AuthenticationServiceTest {

    // Declaration of the AuthenticationService object
    private AuthenticationService authService;

    // Method executed before each test method
    @BeforeEach
    void setUp() {
        // Initialization of the AuthenticationService object
        authService = new AuthenticationService();

        // Clearing test directories before each test
        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
        LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log");
        UserDataService.setTheUserDataDirectoryPath(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_QR_CODE_DIRECTORY);

        // Creating sample user data for testing
        User user1 = new User("JaneDoe", "Jane", "Doe", "JaneDoe", "password123");
        User user2 = new User("JohnDoe", "John", "Doe", "JohnDoe", "password456");

        // Writing user data to files
        UserDataService.writeUserToFile(user1);
        UserDataService.writeUserToFile(user2);
    }

    // Method executed after each test method
    @AfterEach
    public void tearDown() {
        // Cleaning up test directories after each test
        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
        FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
        FileUtils.recursiveDelete(DirectoryPath.TEST_QR_CODE_DIRECTORY);
    }

    // Test for successful login
    @Test
    void testLoginSuccess() {
        User result = authService.login("JaneDoe", "password123");
        assertNotNull(result);
        assertEquals("JaneDoe", result.getUserName());
        assertEquals("password123", result.getPassword());
    }

    // Test for login failure due to wrong password
    @Test
    void testLoginFailureWrongPassword() {
        User result = authService.login("JaneDoe", "wrongPassword");
        assertNull(result);
    }

    // Test for login failure due to non-existent user
    @Test
    void testLoginFailureNonExistentUser() {
        User result = authService.login("NonExistentUser", "password");
        assertNull(result);
    }

    // Test for saving a new user
    @Test
    void testSaveUser() {
        User newUser = new User("NewUser", "New", "User", "NewUser", "newpassword");
        assertTrue(authService.saveUser(newUser));
        assertNotNull(authService.login("NewUser", "newpassword"));
    }
}
