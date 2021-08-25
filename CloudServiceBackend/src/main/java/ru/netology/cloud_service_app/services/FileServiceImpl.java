package ru.netology.cloud_service_app.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.exceptions.DeleteFileException;
import ru.netology.cloud_service_app.exceptions.DownloadFileException;
import ru.netology.cloud_service_app.exceptions.EditFileException;
import ru.netology.cloud_service_app.exceptions.SaveFileException;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.repositories.FileRepository;
import ru.netology.cloud_service_app.utils.FileUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.DELETED;

@Transactional
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<FileData> getAllFiles(int limit, String login) {
        val uploadedFiles = fileRepository.findAllByUserLoginAndStatus(login, ACTIVE, PageRequest.of(0, limit));
        if (!uploadedFiles.isEmpty()) {
            return uploadedFiles.stream()
                    .map(uploadedFile -> new FileData(uploadedFile.getName(), uploadedFile.getSize()))
                    .collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    @Override
    @SneakyThrows
    public void saveFile(MultipartFile file, String login) {
        val filename = file.getOriginalFilename();
        val hash = FileUtils.getFileHash(filename, login);
        log.info("Осуществляется проверка, возможно ли восстановить удаленный файл");
        if (FileUtils.isFileCashed(hash, file.getBytes())) {
            val fileFromDb = fileRepository.findByHash(hash)
                    .orElseThrow(() -> new SaveFileException(format("Ошибка сохранения файла %s", filename)));
            fileFromDb.setStatus(ACTIVE);
            fileFromDb.setChangeTime(LocalDateTime.now());
            fileFromDb.setRemovalTime(null);
            FileUtils.removeFromCashStorage(hash);
            log.info("Файл восстановлен, его статус обновился");
        } else {
            try {
                log.info("В БД не обнаружено файлов, пригодных для восстановления");
                val user = User.builder().login(login).build();
                val fileToUpload = UploadedFile.builder()
                        .name(filename)
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
                        && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505")) {
                    val message = "Ошибка сохранения файла, дубликаты запрещены";
                    log.info(message);
                    throw new SaveFileException(message);
                }
            }
        }
    }

    @Override
    public void deleteFile(String filename, String login) {
        val hash = FileUtils.getFileHash(filename, login);
        val fileFromDb = fileRepository.findByHash(hash)
                .orElseThrow(() -> {
                    val message = String.format("Ошибка удаления файла %s", filename);
                    log.info(message);
                    return new DeleteFileException(message);
                });
        fileFromDb.setStatus(DELETED);
        fileFromDb.setRemovalTime(LocalDateTime.now());
        FileUtils.cashDeletedFile(hash, fileFromDb.getFileBody());
    }

    @Override
    public UploadedFile downloadFile(String filename, String login) {
        val hash = FileUtils.getFileHash(filename, login);
        return fileRepository.findByHash(hash)
                .orElseThrow(() -> {
                    val message = String.format("Ошибка загрузки файла %s", filename);
                    log.info(message);
                    return new DownloadFileException(message);
                });
    }

    @Override
    public void editFilename(String oldFilename, String newFilename, String login) {
        val oldHash = FileUtils.getFileHash(oldFilename, login);
        val file = fileRepository.findByHash(oldHash).orElseThrow(() -> {
            val message = String.format("Ошибка изменения файла %s", oldFilename);
            log.info(message);
            return new EditFileException(message);
        });
        val newHash = FileUtils.getFileHash(newFilename, login);
        file.setHash(newHash);
        file.setName(newFilename);
        file.setChangeTime(LocalDateTime.now());
    }
}
