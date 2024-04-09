package com.jgm.securepasswordmanager.datamodel;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class contains JUnit tests for the LogEntry class to ensure its correctness.
public class LogEntryTest {

	// Test to ensure the constructor correctly initializes a LogEntry object with the current date and time.
	@Test
	public void constructorCorrectlyInitializesLogEntry() {
		// Given
		String expectedSeverity = "INFO";
		String expectedMessage = "Test message";

		// When
		LogEntry logEntry = new LogEntry(expectedSeverity, expectedMessage);

		// Then
		assertEquals(LocalDate.now().toString(), logEntry.getDate(), "The date should match the current date.");
		assertEquals(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), logEntry.getTime(), "The time should be close to the current time, allowing for minor differences in execution time.");
		assertEquals(expectedSeverity, logEntry.getSeverity(), "The severity should match the input severity.");
		assertEquals(expectedMessage, logEntry.getMessage(), "The message should match the input message.");
	}

	// Test to ensure the setters update the properties correctly.
	@Test
	public void settersUpdatePropertiesCorrectly() {
		// Given
		LogEntry logEntry = new LogEntry("INFO", "Initial message");
		String newDate = "2022-01-01";
		String newTime = "12:00:00";
		String newSeverity = "ERROR";
		String newMessage = "Updated message";

		// When
		logEntry.setDate(newDate);
		logEntry.setTime(newTime);
		logEntry.setSeverity(newSeverity);
		logEntry.setMessage(newMessage);

		// Then
		assertEquals(newDate, logEntry.getDate(), "The date should be updated to the new date.");
		assertEquals(newTime, logEntry.getTime(), "The time should be updated to the new time.");
		assertEquals(newSeverity, logEntry.getSeverity(), "The severity should be updated to the new severity.");
		assertEquals(newMessage, logEntry.getMessage(), "The message should be updated to the new message.");
	}

	// Test to ensure the toString method returns a correctly formatted string.
	@Test
	public void toStringReturnsCorrectFormat() {
		// Given
		String severity = "DEBUG";
		String message = "A debug message";
		LogEntry logEntry = new LogEntry(severity, message);
		String expectedString = "LogEntry{date='" + logEntry.getDate() + '\'' +
				", time='" + logEntry.getTime() + '\'' +
				", severity='" + severity + '\'' +
				", message='" + message + '\'' +
				'}';

		// When
		String actualString = logEntry.toString();

		// Then
		assertEquals(expectedString, actualString, "The toString method should return a string formatted as expected.");
	}
}
