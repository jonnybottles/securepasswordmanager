package com.jgm.securepasswordmanager.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataServiceTest {

    private UserDataService theUserDataService ;

    @BeforeEach
    public void setUp() {
         theUserDataService = new UserDataService();
    }


    @Test
    public void testCreateUserDataDirectory() {

        File userDataDirectory = new File("user_data");
        assertFalse(userDataDirectory.exists());

        boolean result = theUserDataService.createUserDataDirectory();

        // Assert
        assertTrue(result); // Directory should be created successfully.
        assertTrue(userDataDirectory.exists()); // Directory should exist after creation

        // Clean up
        userDataDirectory.delete(); // Delete the created directory after the test

    }


}
