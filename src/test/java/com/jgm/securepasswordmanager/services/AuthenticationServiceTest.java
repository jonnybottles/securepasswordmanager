package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService();

        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);

        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
        LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log"); ;
        UserDataService.setTheUserDataDirectoryPath(DirectoryPath.TEST_USERS_DIRECTORY);
        UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_QR_CODE_DIRECTORY);


        // Simulate UserDataService by directly manipulating test data
        User user1 = new User("JaneDoe", "Jane", "Doe", "JaneDoe", "password123");
        User user2 = new User("JohnDoe", "John", "Doe", "JohnDoe", "password456");


        // Assuming AuthenticationService can directly accept test users
        UserDataService.writeUserToFile(user1);
        UserDataService.writeUserToFile(user2);

    }

    @AfterEach
    public void tearDown() {
        // Clean up after each test by deleting the test directory and its contents
        // Clean up after each test by deleting the test directory and its contents
        FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
        FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
        FileUtils.recursiveDelete(DirectoryPath.TEST_QR_CODE_DIRECTORY);

    }

    @Test
    void testLoginSuccess() {
        User result = authService.login("JaneDoe", "password123");
        assertNotNull(result);
        assertEquals("JaneDoe", result.getUserName());
        assertEquals("password123", result.getPassword());
    }

    @Test
    void testLoginFailureWrongPassword() {
        User result = authService.login("JaneDoe", "wrongPassword");
        assertNull(result);
    }

    @Test
    void testLoginFailureNonExistentUser() {
        User result = authService.login("NonExistentUser", "password");
        assertNull(result);
    }

    // Assuming AuthenticationService has a method to add new users for the purpose of this test
    @Test
    void testSaveUser() {
        User newUser = new User("NewUser", "New", "User", "NewUser", "newpassword");
        assertTrue(authService.saveUser(newUser));
        assertNotNull(authService.login("NewUser", "newpassword"));
    }

}
