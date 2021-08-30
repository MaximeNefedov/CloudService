package ru.netology.cloud_service_app.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import ru.netology.cloud_service_app.models.MultipartFileValidator;
import ru.netology.cloud_service_app.repositories.FileRepository;
import ru.netology.cloud_service_app.utils.FileUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean validateMultipartFile(MultipartFile multipartFile) {
        return MultipartFileValidator.isFileBodyValid()
                .and(MultipartFileValidator.isOriginalFilenameValid())
                .apply(multipartFile);
    }

    @Override
    @SneakyThrows
    public void saveFile(MultipartFile fileToUpload, String login) {
        if (!validateMultipartFile(fileToUpload)) {
            val msg = "Невалидные данные";
            log.error(msg);
            throw new SaveFileException(msg);
        }
        val filename = fileToUpload.getOriginalFilename();
        val hash = FileUtils.getFileHash(filename, login);
        log.info("Осуществляется проверка на наличие дупликата или возможности восстановить файл");
        val fileFromDbOptional = fileRepository.findByHash(hash);
        if (fileFromDbOptional.isPresent()) {
            val fileFromDb = fileFromDbOptional.get();
            val fullFileHashFromDb = fileFromDb.getFullFileHash();
            val fullFileHashUploadToDb = FileUtils.getFullFileHash(filename, login, fileToUpload.getBytes());
            if (fileFromDb.getStatus().equals(DELETED)) {
                if (fullFileHashFromDb.equals(fullFileHashUploadToDb)) {
                    log.info("Файл пригоден для восстановления");
                    fileFromDb.setStatus(ACTIVE);
                    fileFromDb.setChangeTime(LocalDateTime.now());
                    fileFromDb.setRemovalTime(null);
                    log.info("Файл восстановлен, его статус обновился");
                    return;
                } else {
                    fileRepository.deleteByHash(hash);
                    fileRepository.flush();
                    log.info("Прошлая версия файла под тем же хешом удалена из БД");
                }
            } else if (fullFileHashFromDb.equals(fullFileHashUploadToDb)) {
                val msg = "Сохранение дубликтов запрещено";
                log.error(msg);
                throw new SaveFileException(msg);
            } else {
                val msg = "Измените название файла";
                log.error(msg);
                throw new SaveFileException(msg);
            }
        }
        log.info("В БД не обнаружено файлов, пригодных для восстановления");
        val user = User.builder().login(login).build();
        val fileBytes = fileToUpload.getBytes();
        val fileToUploadEntity = UploadedFile.builder()
                .name(filename)
                .contentType(fileToUpload.getContentType())
                .size(fileToUpload.getSize())
                .changeTime(LocalDateTime.now())
                .fileBody(fileBytes)
                .user(user)
                .hash(hash)
                .fullFileHash(FileUtils.getFullFileHash(filename, login, fileBytes))
                .status(ACTIVE)
                .build();
        fileRepository.save(fileToUploadEntity);
    }

    @Override
    public void deleteFile(String filename, String login) {
        val hash = FileUtils.getFileHash(filename, login);
        val fileFromDb = fileRepository.findByHash(hash)
                .orElseThrow(() -> {
                    val msg = String.format("Ошибка удаления файла %s", filename);
                    log.error(msg);
                    return new DeleteFileException(msg);
                });
        fileFromDb.setStatus(DELETED);
        fileFromDb.setRemovalTime(LocalDateTime.now());
    }

    @Override
    public UploadedFile downloadFile(String filename, String login) {
        val hash = FileUtils.getFileHash(filename, login);
        return fileRepository.findByHash(hash)
                .orElseThrow(() -> {
                    val msg = String.format("Ошибка загрузки файла %s", filename);
                    log.error(msg);
                    return new DownloadFileException(msg);
                });
    }

    @Override
    public void editFilename(String oldFilename, String newFilename, String login) {
        val oldHash = FileUtils.getFileHash(oldFilename, login);
        val file = fileRepository.findByHash(oldHash).orElseThrow(() -> {
            val msg = String.format("Ошибка изменения файла %s", oldFilename);
            log.error(msg);
            return new EditFileException(msg);
        });
        val newHash = FileUtils.getFileHash(newFilename, login);
        file.setHash(newHash);
        file.setName(newFilename);
        file.setChangeTime(LocalDateTime.now());
    }
}
