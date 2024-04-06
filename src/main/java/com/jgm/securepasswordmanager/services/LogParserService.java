package com.jgm.securepasswordmanager.services;
import com.jgm.securepasswordmanager.datamodel.LogEntry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserService {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
	private static final Pattern logPattern = Pattern.compile("(\\w{3} \\d{2}, \\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M).*");

	public List<LogEntry> parseLogFile(Reader readerSource) {
		List<LogEntry> entries = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(readerSource)) {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = logPattern.matcher(line);
				if (matcher.find()) {
					// Extract date and time
					String datetimeStr = matcher.group(1);
					LocalDateTime timestamp = LocalDateTime.parse(datetimeStr, formatter);

					// The next line contains the log level and message
					String nextLine = reader.readLine();
					if (nextLine != null) {
						int colonIndex = nextLine.indexOf(':');
						if (colonIndex != -1) {
							String severity = nextLine.substring(0, colonIndex);
							String message = nextLine.substring(colonIndex + 2); // skip ": "
							LogEntry entry = new LogEntry(timestamp, severity, message);
							entries.add(entry);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entries;
	}
}