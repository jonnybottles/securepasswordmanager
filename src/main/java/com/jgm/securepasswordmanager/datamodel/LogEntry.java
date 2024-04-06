package com.jgm.securepasswordmanager.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
	private ObjectProperty<LocalDateTime> timestamp;
	private ObjectProperty<LocalDate> date;
	private ObjectProperty<LocalTime> time;
	private StringProperty severity;
	private StringProperty message;

	public LogEntry(LocalDateTime timestamp, String severity, String message) {
		this.timestamp = new SimpleObjectProperty<>(timestamp);
		this.date = new SimpleObjectProperty<>(timestamp.toLocalDate());
		this.time = new SimpleObjectProperty<>(timestamp.toLocalTime());
		this.severity = new SimpleStringProperty(severity);
		this.message = new SimpleStringProperty(message);
	}

	// Property getters
	public ObjectProperty<LocalDateTime> timestampProperty() {
		return timestamp;
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return date;
	}

	public ObjectProperty<LocalTime> timeProperty() {
		return time;
	}

	public StringProperty severityProperty() {
		return severity;
	}

	public StringProperty messageProperty() {
		return message;
	}

	// Direct value getters
	public LocalDateTime getTimestamp() {
		return timestamp.get();
	}

	public LocalDate getDate() {
		return date.get();
	}

	public LocalTime getTime() {
		return time.get();
	}

	public String getSeverity() {
		return severity.get();
	}

	public String getMessage() {
		return message.get();
	}

	// Setters
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp.set(timestamp);
		this.date.set(timestamp.toLocalDate());
		this.time.set(timestamp.toLocalTime());
	}

	public void setSeverity(String severity) {
		this.severity.set(severity);
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	// Optional: toString method for debugging
	@Override
	public String toString() {
		return "LogEntry{" +
				"date=" + date.get().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
				", time=" + time.get().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
				", severity='" + severity.get() + '\'' +
				", message='" + message.get() + '\'' +
				'}';
	}
}
