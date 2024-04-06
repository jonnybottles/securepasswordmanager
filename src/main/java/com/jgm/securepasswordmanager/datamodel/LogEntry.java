package com.jgm.securepasswordmanager.datamodel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
	private String date; // Now as String
	private String time; // Now as String
	private String severity;
	private String message;

	// Constructor initializes the date and time to the current moment as strings
	public LogEntry(String severity, String message) {
		this.date = java.time.LocalDate.now().toString();
		this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		this.severity = severity;
		this.message = message;
	}

	// Getters and Setters
	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LogEntry{" +
				"date='" + date + '\'' +
				", time='" + time + '\'' +
				", severity='" + severity + '\'' +
				", message='" + message + '\'' +
				'}';
	}
}
