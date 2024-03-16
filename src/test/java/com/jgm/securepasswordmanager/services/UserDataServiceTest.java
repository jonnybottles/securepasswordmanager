package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
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
//        userDataDirectory.delete(); // Delete the created directory after the test

    }

    @Test
    public void testWriteUserToFile() {
        UserDataService theUserDataService = new UserDataService();

        WebsiteCredential theBankCreds = new WebsiteCredential("jpmorgan.com", "jjbutler2004",
                "secretpass3", "My bank account is super secret");

        WebsiteCredential theFacebookCreds = new WebsiteCredential("facebook.com", "jjb29572",
                "facebookiscool88", "I have no friends :(");

        User theUser = new User("Jonathan", "Butler", "xxbutler86xx@gmail.com"
                , "jbutler86", "secretpassword");

        theUser.addCredential(theBankCreds);
        theUser.addCredential(theFacebookCreds);

        assertTrue(theUserDataService.writeUserToFile(theUser));





    }


}
