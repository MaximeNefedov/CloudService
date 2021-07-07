package ru.netology.cloud_service_app.exceptions;

public class DeleteFileException extends RuntimeException {
    public DeleteFileException(String message) {
        super(message);
    }
}
