package ru.netology.cloud_service_app.models.file_uploaders;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;

import java.util.List;
import java.util.Optional;

public interface FileHandler {
    boolean uploadToDb(MultipartFile multipartFile, User user);

    boolean deleteFileFromDb(String filename, String login);

    boolean editFilenameInDb(String oldFilename, String newFilename, String login);

    List<UploadedFile> getAllFiles(int limit, String login);

    Optional<UploadedFile> downloadFile(String filename, String login);

}
