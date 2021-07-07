package ru.netology.cloud_service_app.models.dbhandlers;

public interface DbHandler {
    public boolean isFileWasDeletedRecently(String hash);
}
