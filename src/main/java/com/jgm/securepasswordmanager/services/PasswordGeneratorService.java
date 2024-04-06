package com.jgm.securepasswordmanager.services;

import java.security.SecureRandom;

public class PasswordGeneratorService {
	private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER = UPPER.toLowerCase();
	private static final String NUMBERS = "0123456789";
	private static final String SPECIAL = "!@#$%^&*()_+";

	private static final SecureRandom random = new SecureRandom();

	public static String generatePassword(int length, int numUpperCase, int numLowerCase, int numNumbers, int numSpecialChar) {
		if (length < numUpperCase + numLowerCase + numNumbers + numSpecialChar) {
			throw new IllegalArgumentException("Length is too short for the specified requirements.");
		}

		StringBuilder password = new StringBuilder(length);

		// Add at least one of each required type
		addRandomChars(password, UPPER, numUpperCase);
		addRandomChars(password, LOWER, numLowerCase);
		addRandomChars(password, NUMBERS, numNumbers);
		addRandomChars(password, SPECIAL, numSpecialChar);

		// Fill the remaining length with random characters from all types
		String allTypes = UPPER + LOWER + NUMBERS + SPECIAL;
		addRandomChars(password, allTypes, length - password.length());

		// Shuffle to avoid predictable patterns
		return shuffleString(password.toString());
	}

	private static void addRandomChars(StringBuilder password, String chars, int amount) {
		for (int i = 0; i < amount; i++) {
			password.append(chars.charAt(random.nextInt(chars.length())));
		}
	}

	private static String shuffleString(String string) {
		char[] array = string.toCharArray();
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			char temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
		return new String(array);
	}

}
