package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.LogEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogParserServiceTest {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

	@Test
	public void parseLogEntries() {
		String logContent =
				"Apr 06, 2024 11:45:20 AM com.jgm.securepasswordmanager.Main init\n" +
						"INFO: Program directories created successfully.\n" +
						"Apr 06, 2024 11:45:20 AM com.jgm.securepasswordmanager.Main start\n" +
						"INFO: Starting application\n";

		BufferedReader reader = new BufferedReader(new StringReader(logContent));
		LogParserService service = new LogParserService();
		List<LogEntry> entries = service.parseLogFile(reader);

		Assertions.assertEquals(2, entries.size());
		Assertions.assertEquals(LocalDateTime.of(2024, 4, 6, 11, 45, 20), entries.get(0).getTimestamp());
		Assertions.assertEquals("INFO", entries.get(0).getSeverity());
		Assertions.assertEquals("Program directories created successfully.", entries.get(0).getMessage());

		Assertions.assertEquals(LocalDateTime.of(2024, 4, 6, 11, 45, 20), entries.get(1).getTimestamp());
		Assertions.assertEquals("INFO", entries.get(1).getSeverity());
		Assertions.assertEquals("Starting application", entries.get(1).getMessage());
	}
}