package ru.netology.cloud_service_app.repositories;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.models.FileData;

import java.util.List;

public interface CloudServiceFileRepository {
    List<FileData> getAllFiles(int limit, String login);

    UploadedFile downloadFile(String filename, String login);

    void saveFile(MultipartFile multipartFile, User user);

    void deleteFile(String filename, String login);

    void editFilename(String oldFilename, String newFilename, String login);
}
