package com.example.binbuddy.domain.model;

/**
 * Application error types for proper error handling.
 * Provides structured error information with user-friendly messages.
 */
public class AppError extends Exception {
    
    public enum ErrorType {
        NETWORK_ERROR,
        NOT_FOUND_ERROR,
        PARSE_ERROR,
        DATABASE_ERROR,
        UNKNOWN_ERROR,
        OFFLINE_ERROR,
        TIMEOUT_ERROR,
        SERVER_ERROR,
        INVALID_INPUT_ERROR
    }

    private final ErrorType errorType;
    private final String userMessage;
    private final String technicalMessage;

    private AppError(ErrorType errorType, String userMessage, String technicalMessage, Throwable cause) {
        super(technicalMessage, cause);
        this.errorType = errorType;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }

    /**
     * Create a network error (no internet connection, API unavailable).
     */
    public static AppError networkError(String message, Throwable cause) {
        return new AppError(
            ErrorType.NETWORK_ERROR,
            "No internet connection. Please check your network settings.",
            message != null ? message : "Network error: " + (cause != null ? cause.getMessage() : "Unknown"),
            cause
        );
    }

    /**
     * Create a not found error (product not found in API).
     */
    public static AppError notFoundError(String message) {
        return new AppError(
            ErrorType.NOT_FOUND_ERROR,
            "Product not found. Please check the barcode and try again.",
            message != null ? message : "Product not found",
            null
        );
    }

    /**
     * Create a parse error (invalid JSON, malformed data).
     */
    public static AppError parseError(String message, Throwable cause) {
        return new AppError(
            ErrorType.PARSE_ERROR,
            "Unable to process product data. Please try again.",
            message != null ? message : "Parse error: " + (cause != null ? cause.getMessage() : "Unknown"),
            cause
        );
    }

    /**
     * Create a database error (Room database operation failed).
     */
    public static AppError databaseError(String message, Throwable cause) {
        return new AppError(
            ErrorType.DATABASE_ERROR,
            "Unable to save or retrieve data. Please try again.",
            message != null ? message : "Database error: " + (cause != null ? cause.getMessage() : "Unknown"),
            cause
        );
    }

    /**
     * Create an offline error (no network, but cached data available).
     */
    public static AppError offlineError(String message) {
        return new AppError(
            ErrorType.OFFLINE_ERROR,
            "No internet connection. Showing cached data.",
            message != null ? message : "Offline mode",
            null
        );
    }

    /**
     * Create a timeout error (request took too long).
     */
    public static AppError timeoutError(String message, Throwable cause) {
        return new AppError(
            ErrorType.TIMEOUT_ERROR,
            "Request timed out. Please check your connection and try again.",
            message != null ? message : "Timeout error: " + (cause != null ? cause.getMessage() : "Unknown"),
            cause
        );
    }

    /**
     * Create a server error (5xx status codes).
     */
    public static AppError serverError(int statusCode, String message) {
        return new AppError(
            ErrorType.SERVER_ERROR,
            "Server error. Please try again later.",
            message != null ? message : "Server error: HTTP " + statusCode,
            null
        );
    }

    /**
     * Create an invalid input error (bad barcode format, empty query, etc.).
     */
    public static AppError invalidInputError(String userMessage) {
        return new AppError(
            ErrorType.INVALID_INPUT_ERROR,
            userMessage != null ? userMessage : "Invalid input. Please check your entry.",
            "Invalid input error",
            null
        );
    }

    /**
     * Create an unknown error (unexpected error).
     */
    public static AppError unknownError(String message, Throwable cause) {
        return new AppError(
            ErrorType.UNKNOWN_ERROR,
            "An unexpected error occurred. Please try again.",
            message != null ? message : "Unknown error: " + (cause != null ? cause.getMessage() : "Unknown"),
            cause
        );
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Get user-friendly error message to display in UI.
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Get technical error message for logging/debugging.
     */
    public String getTechnicalMessage() {
        return technicalMessage;
    }

    /**
     * Check if this is a network-related error.
     */
    public boolean isNetworkError() {
        return errorType == ErrorType.NETWORK_ERROR || 
               errorType == ErrorType.OFFLINE_ERROR ||
               errorType == ErrorType.TIMEOUT_ERROR;
    }

    /**
     * Check if this is a recoverable error (user can retry).
     */
    public boolean isRecoverable() {
        return errorType == ErrorType.NETWORK_ERROR ||
               errorType == ErrorType.TIMEOUT_ERROR ||
               errorType == ErrorType.SERVER_ERROR ||
               errorType == ErrorType.UNKNOWN_ERROR;
    }
}
