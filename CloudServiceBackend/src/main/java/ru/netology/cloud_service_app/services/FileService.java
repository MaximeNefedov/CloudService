package ru.netology.cloud_service_app.services;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.models.FileData;

import java.util.List;

public interface FileService {

    List<FileData> getAllFiles(int limit, String login);

    void saveFile(MultipartFile file, String login);

    void deleteFile(String filename, String login);

    UploadedFile downloadFile(String filename, String login);

    void editFilename(String oldFilename, String newFilename, String login);

}
