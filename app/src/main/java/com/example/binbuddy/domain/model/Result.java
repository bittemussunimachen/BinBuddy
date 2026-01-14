package com.example.binbuddy.domain.model;

/**
 * Result wrapper class for handling success and error states.
 * Used in repositories and use cases for proper error handling.
 * 
 * @param <T> The type of data on success
 */
public class Result<T> {
    private final T data;
    private final AppError error;
    private final boolean isSuccess;
    private final boolean isFromCache;

    private Result(T data, AppError error, boolean isSuccess, boolean isFromCache) {
        this.data = data;
        this.error = error;
        this.isSuccess = isSuccess;
        this.isFromCache = isFromCache;
    }

    /**
     * Create a successful result.
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, true, false);
    }

    /**
     * Create a successful result from cache.
     */
    public static <T> Result<T> successFromCache(T data) {
        return new Result<>(data, null, true, true);
    }

    /**
     * Create an error result with AppError.
     */
    public static <T> Result<T> error(AppError error) {
        return new Result<>(null, error, false, false);
    }

    /**
     * Create an error result with a Throwable (converts to AppError).
     */
    public static <T> Result<T> error(Throwable throwable) {
        AppError appError = AppError.unknownError(
            throwable != null ? throwable.getMessage() : "Unknown error",
            throwable
        );
        return new Result<>(null, appError, false, false);
    }

    /**
     * Create an error result with a message (converts to AppError).
     */
    public static <T> Result<T> error(String message) {
        AppError appError = AppError.unknownError(message, null);
        return new Result<>(null, appError, false, false);
    }

    /**
     * Create a network error result.
     */
    public static <T> Result<T> networkError(String message, Throwable cause) {
        return error(AppError.networkError(message, cause));
    }

    /**
     * Create a not found error result.
     */
    public static <T> Result<T> notFoundError(String message) {
        return error(AppError.notFoundError(message));
    }

    /**
     * Create a parse error result.
     */
    public static <T> Result<T> parseError(String message, Throwable cause) {
        return error(AppError.parseError(message, cause));
    }

    /**
     * Create a database error result.
     */
    public static <T> Result<T> databaseError(String message, Throwable cause) {
        return error(AppError.databaseError(message, cause));
    }

    /**
     * Create an offline error result (with optional cached data).
     */
    public static <T> Result<T> offlineError(T cachedData, String message) {
        if (cachedData != null) {
            // Return cached data with offline warning
            return new Result<>(cachedData, AppError.offlineError(message), true, true);
        } else {
            // No cached data available
            return error(AppError.offlineError(message));
        }
    }

    /**
     * Check if the result is successful.
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Check if the result is an error.
     */
    public boolean isError() {
        return !isSuccess;
    }

    /**
     * Get the data (only valid if isSuccess() returns true).
     */
    public T getData() {
        return data;
    }

    /**
     * Get the error (only valid if isError() returns true).
     */
    public AppError getError() {
        return error;
    }

    /**
     * Get the error message (user-friendly).
     */
    public String getErrorMessage() {
        if (error != null) {
            return error.getUserMessage();
        }
        return "Unknown error";
    }

    /**
     * Get the technical error message (for logging).
     */
    public String getTechnicalErrorMessage() {
        if (error != null) {
            return error.getTechnicalMessage();
        }
        return "Unknown error";
    }

    /**
     * Check if this result is from cache.
     */
    public boolean isFromCache() {
        return isFromCache;
    }

    /**
     * Check if this is a network-related error.
     */
    public boolean isNetworkError() {
        return error != null && error.isNetworkError();
    }

    /**
     * Check if this error is recoverable (user can retry).
     */
    public boolean isRecoverable() {
        return error != null && error.isRecoverable();
    }
}
