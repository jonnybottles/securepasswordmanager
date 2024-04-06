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
	private static final String LOG_FILE_PATH = DirectoryPath.LOGS_DIRECTORY + "/securepasswordmanager.log";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static {
		// Ensure the logs directory exists
		new File(DirectoryPath.LOGS_DIRECTORY).mkdirs();
	}

	public static void appendLog(LogEntry logEntry) {
		List<LogEntry> logEntries = loadLogs();
		logEntries.add(logEntry); // Now safe, logEntries cannot be null
		try (Writer writer = new FileWriter(LOG_FILE_PATH)) {
			gson.toJson(logEntries, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<LogEntry> loadLogs() {
		// Ensures we return an empty list instead of null if anything goes wrong
		List<LogEntry> logEntries = new ArrayList<>();
		File logFile = new File(LOG_FILE_PATH);
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
