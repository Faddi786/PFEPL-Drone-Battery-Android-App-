package com.example.udaan;

public class UploadResult {
    private boolean success;
    private String errorMessage;

    public UploadResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
