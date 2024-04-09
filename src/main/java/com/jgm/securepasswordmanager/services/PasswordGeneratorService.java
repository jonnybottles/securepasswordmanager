package com.jgm.securepasswordmanager.services;

// Importing the SecureRandom class for generating cryptographically strong random values.
import java.security.SecureRandom;

public class PasswordGeneratorService {
	// Define character sets to be used in password generation.
	private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER = UPPER.toLowerCase(); // Dynamically generate lowercase letters from uppercase.
	private static final String NUMBERS = "0123456789";
	private static final String SPECIAL = "!@#$%^&*()_+";

	// Instantiate a SecureRandom object for secure random number generation.
	private static final SecureRandom random = new SecureRandom();

	// Main method for generating a password based on input criteria.
	public static String generatePassword(int length, int numUpperCase, int numLowerCase, int numNumbers, int numSpecialChar) {
		// Validate that the total length is sufficient for the specified character types.
		if (length < numUpperCase + numLowerCase + numNumbers + numSpecialChar) {
			throw new IllegalArgumentException("Length is too short for the specified requirements.");
		}

		StringBuilder password = new StringBuilder(length);

		// Add the specified number of characters of each type to the password.
		addRandomChars(password, UPPER, numUpperCase);
		addRandomChars(password, LOWER, numLowerCase);
		addRandomChars(password, NUMBERS, numNumbers);
		addRandomChars(password, SPECIAL, numSpecialChar);

		// Fill the rest of the password length with random characters from all types.
		String allTypes = UPPER + LOWER + NUMBERS + SPECIAL;
		addRandomChars(password, allTypes, length - password.length());

		// Shuffle the characters to eliminate any predictable patterns.
		return shuffleString(password.toString());
	}

	// Helper method to add a specified number of random characters from a given character set to the password.
	private static void addRandomChars(StringBuilder password, String chars, int amount) {
		for (int i = 0; i < amount; i++) {
			password.append(chars.charAt(random.nextInt(chars.length())));
		}
	}

	// Helper method to shuffle the characters in the password to ensure randomness.
	private static String shuffleString(String string) {
		char[] array = string.toCharArray();
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Swap characters at indices 'index' and 'i'.
			char temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
		return new String(array);
	}
}
