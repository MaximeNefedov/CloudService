package ru.netology.cloud_service_app.services;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.models.file_uploaders.FileUploader;
import ru.netology.cloud_service_app.repositories.FileRepository;
import ru.netology.cloud_service_app.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileUploader fileUploader;

    public FileService(FileRepository fileRepository,
                       UserRepository userRepository,
                       @Qualifier("defaultFileUploader") FileUploader fileUploader) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileUploader = fileUploader;
    }

    @Transactional
    public List<FileData> getAllFiles(int limit, String login) {
        System.out.println(login);
        val files = fileRepository.findFileByUserLogin(login, PageRequest.of(0, limit));
        List<FileData> fileDataList = new ArrayList<>(limit);
        for (UploadedFile uploadedFile : files) {
            fileDataList.add(new FileData(uploadedFile.getName(), uploadedFile.getSize()));
        }
        return fileDataList;
    }

    public void saveFile(MultipartFile multipartFile, String login) {
        val user = userRepository.findByLogin(login)
                .orElseThrow(IllegalArgumentException::new);
        fileUploader.uploadToDb(multipartFile, user);
    }

    public UploadedFile downloadFile(String filename, String login) {
        return fileRepository.findByNameAndUserLogin(filename, login)
                .orElseThrow(IllegalArgumentException::new);
    }
}
