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

public class LogParserServiceTest {

	@BeforeEach
	public void setup() {
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
		LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log"); ;
	}

	@AfterEach
	public void teardown() throws IOException {
		FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);

	}

	@Test
	public void parseLogEntries() throws IOException {

		// Simulate log entries
		LogEntry logEntry1 = new LogEntry("INFO", "Program directories created successfully.");
		logEntry1.setDate("2024-04-06"); ; // yyyy-MM-dd
		logEntry1.setTime("11:45:20"); // HH:mm:ss

		LogEntry logEntry2 = new LogEntry("INFO", "Starting application");
		logEntry2.setDate("2024-04-06"); ; // yyyy-MM-dd
		logEntry2.setTime("11:45:20"); ; // HH:mm:ss

		// Manually append log entries to the log file
		LogParserService.appendLog(logEntry1);
		LogParserService.appendLog(logEntry2);

		// Act
		List<LogEntry> entries = LogParserService.loadLogs();

		// Assert
		Assertions.assertEquals(2, entries.size());

		Assertions.assertEquals("2024-04-06", entries.get(0).getDate());
		Assertions.assertEquals("11:45:20", entries.get(0).getTime());
		Assertions.assertEquals("INFO", entries.get(0).getSeverity());
		Assertions.assertEquals("Program directories created successfully.", entries.get(0).getMessage());

		Assertions.assertEquals("2024-04-06", entries.get(1).getDate());
		Assertions.assertEquals("11:45:20", entries.get(1).getTime());
		Assertions.assertEquals("INFO", entries.get(1).getSeverity());
		Assertions.assertEquals("Starting application", entries.get(1).getMessage());
	}
}
