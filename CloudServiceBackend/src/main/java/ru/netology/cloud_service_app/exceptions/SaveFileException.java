package ru.netology.cloud_service_app.exceptions;

public class SaveFileException extends RuntimeException {
    public SaveFileException(String message) {
        super(message);
    }
}
