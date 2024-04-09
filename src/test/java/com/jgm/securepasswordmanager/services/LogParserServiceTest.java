package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;

// Defines a test class for the LogParserService, ensuring log parsing functionality works as expected
public class LogParserServiceTest {

	// Setup method to run before each test case. Ensures the test log directory exists and sets the log file path.
	@BeforeEach
	public void setup() {
		// Create the test logs directory if it doesn't exist
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
		// Set the path for the log file within the test logs directory
		LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log");
	}

	// Teardown method to run after each test case. Cleans up by deleting the test logs directory and its contents.
	@AfterEach
	public void teardown() throws IOException {
		// Delete the test logs directory and all of its contents to ensure a clean state for subsequent tests
		FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
	}

	// Test case to verify the parsing of log entries from the log file works as expected
	@Test
	public void parseLogEntries() throws IOException {

		// Simulate creation of log entries to be tested
		LogEntry logEntry1 = new LogEntry("INFO", "Program directories created successfully.");
		// Set the date and time for the first log entry
		logEntry1.setDate("2024-04-06"); // yyyy-MM-dd format
		logEntry1.setTime("11:45:20"); // HH:mm:ss format

		LogEntry logEntry2 = new LogEntry("INFO", "Starting application");
		// Set the date and time for the second log entry
		logEntry2.setDate("2024-04-06"); // yyyy-MM-dd format
		logEntry2.setTime("11:45:20"); // HH:mm:ss format

		// Manually append the simulated log entries to the log file for testing
		LogParserService.appendLog(logEntry1);
		LogParserService.appendLog(logEntry2);

		// Action: Load the log entries from the log file
		List<LogEntry> entries = LogParserService.loadLogs();

		// Assertions to validate the expected outcomes of the test case
		// Verify that exactly 2 log entries were loaded
		Assertions.assertEquals(2, entries.size());

		// Verify the details of the first loaded log entry
		Assertions.assertEquals("2024-04-06", entries.get(0).getDate());
		Assertions.assertEquals("11:45:20", entries.get(0).getTime());
		Assertions.assertEquals("INFO", entries.get(0).getSeverity());
		Assertions.assertEquals("Program directories created successfully.", entries.get(0).getMessage());

		// Verify the details of the second loaded log entry
		Assertions.assertEquals("2024-04-06", entries.get(1).getDate());
		Assertions.assertEquals("11:45:20", entries.get(1).getTime());
		Assertions.assertEquals("INFO", entries.get(1).getSeverity());
		Assertions.assertEquals("Starting application", entries.get(1).getMessage());
	}
}
