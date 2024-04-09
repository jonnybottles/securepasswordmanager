package com.jgm.securepasswordmanager.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jgm.securepasswordmanager.datamodel.LogEntry;
import com.jgm.securepasswordmanager.utils.DirectoryPath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LogParserService {
	private static String logFilePath = DirectoryPath.LOGS_DIRECTORY + "/securepasswordmanager.log";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

//	static {
//		// Ensure the logs directory exists
//		new File(DirectoryPath.LOGS_DIRECTORY).mkdirs();
//	}

	// Static method to set the logs file path, which also ensures the directory exists
	public static void setLogFilePath(String newPath) {
		logFilePath = newPath;
		// Ensure the logs directory exists
		new File(logFilePath).getParentFile().mkdirs();
	}

	public static void appendLog(LogEntry logEntry) {
		List<LogEntry> logEntries = loadLogs();
		logEntries.add(logEntry); // Now safe, logEntries cannot be null
		try (Writer writer = new FileWriter(logFilePath)) {
			gson.toJson(logEntries, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<LogEntry> loadLogs() {
		// Ensures we return an empty list instead of null if anything goes wrong
		List<LogEntry> logEntries = new ArrayList<>();
		File logFile = new File(logFilePath);
		if (logFile.exists()) {
			try (Reader reader = new FileReader(logFile)) {
				// This line potentially returned null; fixed by ensuring an empty list is returned on error
				logEntries = gson.fromJson(reader, new TypeToken<List<LogEntry>>(){}.getType());
				if (logEntries == null) {
					logEntries = new ArrayList<>();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logEntries;
	}


}
