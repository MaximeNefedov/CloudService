package ru.netology.cloud_service_app.models.file_uploaders;

import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.exceptions.SaveFileException;
import ru.netology.cloud_service_app.models.dbhandlers.DbHandler;
import ru.netology.cloud_service_app.repositories.file_repositories.FileRepository;
import ru.netology.cloud_service_app.configs.FileUploaderConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.DELETED;

@Component
public class DefaultFileHandler implements FileHandler {

    private final FileRepository fileRepository;
    private final FileUploaderConfiguration fileUploaderConfiguration;
    private final DbHandler dbHandler;

    public DefaultFileHandler(FileRepository fileRepository,
                              FileUploaderConfiguration fileUploaderConfiguration,
                              DbHandler dbHandler) {
        this.fileRepository = fileRepository;
        this.fileUploaderConfiguration = fileUploaderConfiguration;
        this.dbHandler = dbHandler;
    }

    public String getFileHash(String filename, String login) {
        val contentType = getContentType(filename);
        return getHash(filename, login, contentType);
    }

    @Override
    public boolean deleteFileFromDb(String filename, String login) {
        val hash = getFileHash(filename, login);
        val fileOptional = fileRepository.findByHash(hash);
        if (fileOptional.isPresent()) {
            val file = fileOptional.get();
            file.setStatus(DELETED);
            file.setRemovalTime(LocalDateTime.now());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean editFilenameInDb(String oldFilename, String newFilename, String login) {
        val hash = getFileHash(oldFilename, login);
        val fileOptional = fileRepository.findByHash(hash);
        if (fileOptional.isPresent()) {
            val file = fileOptional.get();
            val newHash = getFileHash(newFilename, file.getUser().getLogin());
            file.setHash(newHash);
            file.setName(newFilename);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<UploadedFile> getAllFiles(int limit, String login) {
        return fileRepository.findAllByUserLoginAndStatus(login, ACTIVE, PageRequest.of(0, limit));
    }

    @Override
    public Optional<UploadedFile> downloadFile(String filename, String login) {
        val hash = getFileHash(filename, login);
        return fileRepository.findByHash(hash);
    }

    @Override
    public boolean uploadToDb(MultipartFile multipartFile, User user) {
        try {
            val login = user.getLogin();
            val filename = multipartFile.getOriginalFilename();
            val contentType = multipartFile.getContentType();
            val hash = getHash(filename, login, contentType);
            val fileToUpload = UploadedFile.builder()
                    .name(filename)
                    .contentType(contentType)
                    .size(multipartFile.getSize())
                    .changeTime(LocalDateTime.now())
                    .fileBody(multipartFile.getBytes())
                    .user(user)
                    .hash(hash)
                    .status(ACTIVE)
                    .build();

            if (dbHandler.isFileWasDeletedRecently(hash)) {
                fileRepository.setActiveFileStatus(hash, ACTIVE);
            } else {
                fileRepository.save(fileToUpload);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                    && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
                throw new SaveFileException("Ошибка сохранения файла, дубликаты запрещены");
        }
        return true;
    }

    private String getHash(String filename, String login, String contentType) {
        val stringBuilder = new StringBuilder();
        val bytesForHashing = stringBuilder.append(login)
                .append(filename)
                .append(contentType)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(bytesForHashing);
    }

    private String getContentType(String filename) {
        val fileType = filename.substring(filename.indexOf(".") + 1);
        val contentTypePart = fileUploaderConfiguration.getMimeTypesMap().get(fileType);
        return contentTypePart + fileUploaderConfiguration.getMimeTypesDelimiter() + fileType;
    }
}
