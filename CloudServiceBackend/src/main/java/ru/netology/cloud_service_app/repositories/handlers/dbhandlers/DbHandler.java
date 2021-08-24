package ru.netology.cloud_service_app.repositories.handlers.dbhandlers;

public interface DbHandler {
    boolean isFileAbleToBeRestored(String hash, byte[] fileBytes);
}
