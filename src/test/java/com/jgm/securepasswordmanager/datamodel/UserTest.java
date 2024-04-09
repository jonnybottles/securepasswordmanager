package com.jgm.securepasswordmanager.datamodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

	private User user;

	// Initialize User object before each test
	@BeforeEach
	public void setUp() {
		user = new User("John", "Doe", "john.doe@example.com", "johndoe", "password123");
	}

	// Test setting and getting the first name
	@Test
	public void testSetAndGetFirstName() {
		Assertions.assertTrue(user.setFirstName("Jane"), "Setting first name should succeed");
		Assertions.assertEquals("Jane", user.getFirstName(), "First name should be updated correctly.");
	}

	// Test setting and getting the last name
	@Test
	public void testSetAndGetLastName() {
		Assertions.assertTrue(user.setLastName("Smith"), "Setting last name should succeed");
		Assertions.assertEquals("Smith", user.getLastName(), "Last name should be updated correctly.");
	}

	// Test setting and validating email address
	@Test
	public void testSetAndGetEmailAddress() {
		Assertions.assertTrue(user.setEmailAddress("jane.smith@example.com"), "Setting email should succeed");
		Assertions.assertEquals("jane.smith@example.com", user.getEmailAddress(), "Email should be updated correctly.");
	}

	// Test setting and getting username
	@Test
	public void testSetAndGetUserName() {
		Assertions.assertTrue(user.setUserName("janesmith"), "Setting username should succeed");
		Assertions.assertEquals("janesmith", user.getUserName(), "Username should be updated correctly.");
	}

	// Test setting and getting password
	@Test
	public void testSetAndGetPassword() {
		Assertions.assertTrue(user.setPassword("newPassword123"), "Setting password should succeed");
		Assertions.assertEquals("newPassword123", user.getPassword(), "Password should be updated correctly.");
	}

	// Test two-factor authentication registration
	@Test
	public void testTwoFactorAuthenticationRegistration() {
		user.setHasRegisteredTwoFactorAuthentication(true);
		Assertions.assertTrue(user.getHasRegisteredTwoFactorAuthentication(), "Two-factor authentication flag should be true.");
	}

	// Test setting and getting secret key for 2FA
	@Test
	public void testSetAndGetSecretKeyFor2FABarcode() {
		user.setSecretKeyFor2FABarcode("secretKey");
		Assertions.assertEquals("secretKey", user.getSecretKeyFor2FABarcode(), "Secret key for 2FA should be set correctly.");
	}

	// Test equality based on username and password
	@Test
	public void testEquals() {
		User anotherUser = new User("John", "Doe", "john.doe@example.com", "johndoe", "password123");
		Assertions.assertEquals(user, anotherUser, "Two users with the same username and password should be equal.");
	}

	// Testing hashCode for consistency with equals
	@Test
	public void testHashCode() {
		User anotherUser = new User("John", "Doe", "john.doe@example.com", "johndoe", "password123");
		Assertions.assertEquals(user.hashCode(), anotherUser.hashCode(), "Hash codes of equal users should be the same.");
	}

	// Test setting and getting master password
	@Test
	public void testSetAndGetMasterPassword() {
		user.setMasterPassword("masterPassword!234");
		Assertions.assertEquals("masterPassword!234", user.getMasterPassword(), "Master password should be set and retrieved correctly.");
	}

	// Test the creation and verification of a master password
	@Test
	public void testHasCreatedMasterPassword() {
		user.setHasCreatedMasterPassword(true);
		Assertions.assertTrue(user.getHasCreatedMasterPassword(), "Master password creation flag should be correctly set and retrieved.");
	}

	// Test email validation with an invalid email address
	@Test
	public void testInvalidEmailSet() {
		Assertions.assertFalse(user.setEmailAddress("invalid-email"), "Setting an invalid email should fail.");
	}

	// Test the toString method, mainly to ensure it doesn't throw any exceptions and returns a non-null value
	@Test
	public void testToStringMethod() {
		Assertions.assertNotNull(user.toString(), "The toString method should return a non-null value.");
	}


}
