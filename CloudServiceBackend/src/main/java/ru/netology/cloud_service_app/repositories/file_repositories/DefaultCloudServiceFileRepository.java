package ru.netology.cloud_service_app.repositories.file_repositories;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.exceptions.DeleteFileException;
import ru.netology.cloud_service_app.exceptions.DownloadFileException;
import ru.netology.cloud_service_app.exceptions.EditFileException;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.handlers.file_uploaders.FileHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Repository
public class DefaultCloudServiceFileRepository implements CloudServiceFileRepository {
    private final FileHandler fileHandler;

    public DefaultCloudServiceFileRepository(@Qualifier("defaultFileHandler") FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public List<FileData> getAllFiles(int limit, String login) {
        val files = fileHandler.getAllFiles(limit, login);
        if (files.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<FileData> fileDataList = new ArrayList<>(limit);
            for (UploadedFile uploadedFile : files) {
                fileDataList.add(new FileData(uploadedFile.getName(), uploadedFile.getSize()));
            }
            return fileDataList;
        }
    }

    @Override
    public UploadedFile downloadFile(String filename, String login) {
        return fileHandler.downloadFile(filename, login)
                .orElseThrow(
                        () -> new DownloadFileException(format("Ошибка скачивания файла %s", filename))
                );
    }

    @Override
    public void saveFile(MultipartFile multipartFile, User user) {
        fileHandler.uploadToDb(multipartFile, user);
    }

    @Override
    public void deleteFile(String filename, String login) {
        if (!fileHandler.deleteFileFromDb(filename, login))
            throw new DeleteFileException(format("Ошибка удаления файла %s", filename));
    }

    @Override
    public void editFilename(String oldFilename, String newFilename, String login) {
        if (!fileHandler.editFilenameInDb(oldFilename, newFilename, login)) {
            throw new EditFileException(format("Ошибка изменения файла %s ", oldFilename));
        }
    }
}
