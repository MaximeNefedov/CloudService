package ru.netology.cloud_service_app.services;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.UploadedFileStatus;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.exceptions.DeleteFileException;
import ru.netology.cloud_service_app.exceptions.SaveFileException;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.repositories.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.*;

@Transactional
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public List<FileData> getAllFiles(int limit, String login) {
        val uploadedFiles = fileRepository.findAllByUserLoginAndStatus(login, ACTIVE, PageRequest.of(0, limit));
        if (!uploadedFiles.isEmpty()) {
            return uploadedFiles.stream()
                    .map(uploadedFile -> new FileData(uploadedFile.getName(), uploadedFile.getSize()))
                    .collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    @SneakyThrows
    public void saveFile(MultipartFile file, String login) {
        val fileName = file.getOriginalFilename();
        val hash = FileUtils.getFileHash(fileName, login);
        if (FileUtils.isFileCashed(hash, file.getBytes())) {
            val fileFromDb = fileRepository.findByHash(hash)
                    .orElseThrow(() -> new SaveFileException(format("Ошибка сохранения файла %s", fileName)));
            fileFromDb.setStatus(ACTIVE);
            fileFromDb.setChangeTime(LocalDateTime.now());
            fileFromDb.setRemovalTime(null);
            FileUtils.removeFromCashStorage(hash);
        } else {
            try {

                val user = User.builder().login(login).id(10L).build();
                val fileToUpload = UploadedFile.builder()
                        .name(fileName)
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .changeTime(LocalDateTime.now())
                        .fileBody(file.getBytes())
                        .user(user)
                        .hash(hash)
                        .status(ACTIVE)
                        .build();
                fileRepository.save(fileToUpload);
            } catch (DataIntegrityViolationException e) {
                if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                        && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
                    throw new SaveFileException("Ошибка сохранения файла, дубликаты запрещены");
            }
        }
    }

    public void deleteFile(String filename, String login) {
        val hash = FileUtils.getFileHash(filename, login);
        val fileFromDb = fileRepository.findByHash(hash)
                .orElseThrow(() -> new DeleteFileException(format("Ошибка удаления файла %s", filename)));
        fileFromDb.setStatus(DELETED);
        fileFromDb.setRemovalTime(LocalDateTime.now());
        FileUtils.cashDeletedFile(hash, fileFromDb.getFileBody());
    }

//    private final CloudServiceFileRepository cloudServiceFileRepository;
//    private final CloudServiceUserRepository cloudServiceUserRepository;

//    public FileService(CloudServiceFileRepository cloudServiceFileRepository,
//                       CloudServiceUserRepository cloudServiceUserRepository) {
//        this.cloudServiceFileRepository = cloudServiceFileRepository;
//        this.cloudServiceUserRepository = cloudServiceUserRepository;
//    }
//
//    public List<FileData> getAllFiles(int limit, String login) {
//        return cloudServiceFileRepository.getAllFiles(limit, login);
//    }
//
//    public void saveFile(MultipartFile multipartFile, String login) {
//        val user = cloudServiceUserRepository.findUserByLogin(login);
//        cloudServiceFileRepository.saveFile(multipartFile, user);
//    }
//
//    public UploadedFile downloadFile(String filename, String login) {
//        return cloudServiceFileRepository.downloadFile(filename, login);
//    }
//
//    public void deleteFile(String filename, String login) {
//        cloudServiceFileRepository.deleteFile(filename, login);
//    }
//
//    public void editFilename(String oldFilename, String newFilename, String login) {
//        cloudServiceFileRepository.editFilename(oldFilename, newFilename, login);
//    }
}
