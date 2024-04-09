
package com.jgm.securepasswordmanager.datamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// This class tests the functionality of the WebsiteCredential model within the Secure Password Manager application.
// It ensures that getters, setters, and essential methods like equals() and hashCode() operate as expected.
class WebsiteCredentialTest {

    // Instance variable to hold the WebsiteCredential object used across all the tests.
    private WebsiteCredential credential;

    @BeforeEach
    void setUp() {
        // Initializes a WebsiteCredential object with predefined values before each test method is executed.
        credential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
    }

    @Test
    void getWebSiteNameReturnsCorrectName() {
        // Tests if the website name getter returns the correct name set during initialization.
        assertEquals("ExampleSite", credential.getWebSiteName());
    }

    @Test
    void setWebSiteNameUpdatesNameCorrectly() {
        // Tests if the website name setter correctly updates the website name.
        credential.setWebSiteName("NewSiteName");
        assertEquals("NewSiteName", credential.getWebSiteName());
    }

    // Note: Similar tests should be written for other getters and setters.

    @Test
    void equalsSelfReturnsTrue() {
        // Tests if the equals method correctly identifies the same object as equal.
        assertTrue(credential.equals(credential));
    }

    @Test
    void equalsNullReturnsFalse() {
        // Tests if the equals method correctly identifies null as not equal.
        assertFalse(credential.equals(null));
    }

    @Test
    void equalsDifferentClassReturnsFalse() {
        // Tests if the equals method correctly identifies objects of different classes as not equal.
        assertFalse(credential.equals("A String Object"));
    }

    @Test
    void equalsDifferentCredentialsReturnsFalse() {
        // Tests if the equals method correctly identifies different WebsiteCredential objects as not equal.
        WebsiteCredential differentCredential = new WebsiteCredential("OtherSite", "User2", "Password123", "Notes");
        assertFalse(credential.equals(differentCredential));
    }

    @Test
    void equalsSameFieldsReturnsTrue() {
        // Tests if the equals method correctly identifies WebsiteCredential objects with the same fields as equal.
        WebsiteCredential sameCredential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
        assertTrue(credential.equals(sameCredential));
    }

    @Test
    void hashCodeConsistentWithEquals() {
        // Tests if the hashCode method is consistent with the equals method,
        // meaning that if two objects are equal according to the equals method,
        // then calling the hashCode method on each of the two objects must produce the same integer result.
        WebsiteCredential sameCredential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
        assertEquals(credential.hashCode(), sameCredential.hashCode());
    }
}
