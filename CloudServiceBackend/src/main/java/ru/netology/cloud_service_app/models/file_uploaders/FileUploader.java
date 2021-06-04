package ru.netology.cloud_service_app.models.file_uploaders;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.User;

public interface FileUploader {
//    void uploadFilesToApplicationDir(FileContainer fileContainer);
//
//    FileData uploadFileFromApplicationDir(FileData fileData);
//
//    void deleteFile(FileContainer fileContainer);
//
//    void renameFile(FileContainer fileContainer);

    void uploadToLocal(MultipartFile multipartFile);

    void uploadToDb(MultipartFile multipartFile, User user);
//    void uploadFilesToApplicationDir(FileContainer fileContainer);
//
//    FileData uploadFileFromApplicationDir(FileData fileData);
//
//    void deleteFile(FileContainer fileContainer);
//
//    void renameFile(FileContainer fileContainer);
}
