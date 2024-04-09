package com.jgm.securepasswordmanager.services;

// Imports necessary libraries and classes for the service.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.utils.DirectoryPath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LogParserService {
	// Defines the path to the log file, dynamically constructed using a directory path from DirectoryPath class.
	private static String logFilePath = DirectoryPath.LOGS_DIRECTORY + "/securepasswordmanager.log";

	// Initializes Gson with pretty printing enabled for readability.
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	// Allows changing the default log file path and ensures the log directory exists.
	public static void setLogFilePath(String newPath) {
		logFilePath = newPath;
		// Ensures the parent directory of the log file exists, creating it if necessary.
		new File(logFilePath).getParentFile().mkdirs();
	}

	// Appends a new log entry to the existing log file.
	public static void appendLog(LogEntry logEntry) {
		// Loads existing log entries from the file.
		List<LogEntry> logEntries = loadLogs();
		// Adds the new log entry to the list.
		logEntries.add(logEntry);
		try (Writer writer = new FileWriter(logFilePath)) {
			// Serializes the updated list of log entries and writes them back to the file.
			gson.toJson(logEntries, writer);
		} catch (IOException e) {
			// Prints the stack trace in case of an IOException.
			e.printStackTrace();
		}
	}

	// Loads the log entries from the log file.
	public static List<LogEntry> loadLogs() {
		// Initializes an empty list to hold the log entries.
		List<LogEntry> logEntries = new ArrayList<>();
		File logFile = new File(logFilePath);
		if (logFile.exists()) {
			try (Reader reader = new FileReader(logFile)) {
				// Deserializes the JSON content of the log file back into a List of LogEntry objects.
				logEntries = gson.fromJson(reader, new TypeToken<List<LogEntry>>(){}.getType());
				// If deserialization returns null (e.g., for an empty file), initializes an empty list to prevent NullPointerException.
				if (logEntries == null) {
					logEntries = new ArrayList<>();
				}
			} catch (IOException e) {
				// Prints the stack trace in case of an IOException.
				e.printStackTrace();
			}
		}
		return logEntries;
	}
}
