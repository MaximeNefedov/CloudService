package ru.netology.cloud_service_app.services;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.repositories.file_repositories.CloudServiceFileRepository;
import ru.netology.cloud_service_app.repositories.user_repositories.CloudServiceUserRepository;

import java.util.List;

@Transactional
@Service
public class FileService {

    private final CloudServiceFileRepository cloudServiceFileRepository;
    private final CloudServiceUserRepository cloudServiceUserRepository;

    public FileService(CloudServiceFileRepository cloudServiceFileRepository,
                       CloudServiceUserRepository cloudServiceUserRepository) {
        this.cloudServiceFileRepository = cloudServiceFileRepository;
        this.cloudServiceUserRepository = cloudServiceUserRepository;
    }

    public List<FileData> getAllFiles(int limit, String login) {
        return cloudServiceFileRepository.getAllFiles(limit, login);
    }

    public void saveFile(MultipartFile multipartFile, String login) {
        val user = cloudServiceUserRepository.findUserByLogin(login);
        cloudServiceFileRepository.saveFile(multipartFile, user);
    }

    public UploadedFile downloadFile(String filename, String login) {
        return cloudServiceFileRepository.downloadFile(filename, login);
    }

    public void deleteFile(String filename, String login) {
        cloudServiceFileRepository.deleteFile(filename, login);
    }

    public void editFilename(String oldFilename, String newFilename, String login) {
        cloudServiceFileRepository.editFilename(oldFilename, newFilename, login);
    }
}
