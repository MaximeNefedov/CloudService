package ru.netology.cloud_service_app.models.file_uploaders;

import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.configs.FileUploaderConfiguration;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.repositories.FileRepository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class DefaultFileUploader implements FileUploader {

    private final FileRepository fileRepository;
    private final FileUploaderConfiguration fileUploaderConfiguration;
    private final String SEPARATOR = File.separator;
    private final String ROOT_DIR_SEPARATOR = "." + File.separator;
    private final String fileType = ".txt";

    public DefaultFileUploader(FileRepository fileRepository, FileUploaderConfiguration fileUploaderConfiguration) {
        this.fileRepository = fileRepository;
        this.fileUploaderConfiguration = fileUploaderConfiguration;
    }

    private String getCurrentDirName(String fileType) {
        val currentType = fileType.substring(0, fileType.indexOf('/'));
        val innerDirNames = fileUploaderConfiguration.getInnerDirNames();
        var defaultName = getDefaultDirNameIfNoneMatched();
        for (String innerDirName : innerDirNames) {
            if (currentType.equals(innerDirName)) {
                defaultName = innerDirName;
            }
        }
        return defaultName;
    }

    private String getDefaultDirNameIfNoneMatched() {
        return fileUploaderConfiguration.getApplicationDirName();
    }

    @Override
    public void uploadToLocal(MultipartFile multipartFile) {
        try {
            val fileBytes = multipartFile.getBytes();
            var contentType = multipartFile.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = getDefaultDirNameIfNoneMatched();
            }
            val currentDirName = getCurrentDirName(contentType);
            val path = Paths.get(ROOT_DIR_SEPARATOR +
                    fileUploaderConfiguration.getMainDirName() +
                    SEPARATOR +
                    currentDirName +
                    SEPARATOR +
                    multipartFile.getOriginalFilename());
            Files.write(path, fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadToDb(MultipartFile multipartFile, User user) {
        try {
            val fileToUpload = UploadedFile.builder()
                    .name(multipartFile.getOriginalFilename())
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .date(LocalDateTime.now())
                    .fileBody(multipartFile.getBytes())
                    .user(user)
                    .build();
            fileRepository.save(fileToUpload);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
                System.out.println("Ошибка сохранения файла");
        }

    }

    @PostConstruct
    private void createApplicationFolders() throws IOException {
        val mainDirPath = Path.of(fileUploaderConfiguration.getMainDirName());
        if (Files.notExists(mainDirPath)) {
            Files.createDirectory(mainDirPath);
            val innerDirNames = fileUploaderConfiguration.getInnerDirNames();
            innerDirNames.stream()
                    .map(Path::of)
                    .forEach(path -> {
                        try {
                            Files.createDirectory(mainDirPath.resolve(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

//    private String getCurrentDirName(FileData fileData) {
//        val contentTypePart = fileData.getContentType().split("/")[0];
//        val innerDirNames = fileUploaderConfiguration.getInnerDirNames();
//        String defaultName = "application";
//        for (String innerDirName : innerDirNames) {
//            if (contentTypePart.equals(innerDirName)) {
//                defaultName = innerDirName;
//            }
//        }
//        return defaultName;
//    }

//    @Override
//    public void uploadFilesToApplicationDir(FileContainer fileContainer) {
//        val files = fileContainer.getFileData();
//        for (FileData fileData : files) {
//            val currentDirName = getCurrentDirName(fileData);
//            try (val out = new ObjectOutputStream(new FileOutputStream(ROOT_DIR_SEPARATOR +
//                    fileUploaderConfiguration.getMainDirName() +
//                    SEPARATOR +
//                    currentDirName +
//                    SEPARATOR +
//                    fileData.getFileName() + fileType))) {
//
//                out.writeObject(fileData);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        val files = fileContainer.getFileData();
//        for (FileData fileData : files) {
//            val currentDirName = getCurrentDirName(fileData);
//            try (val out = new FileOutputStream(ROOT_DIR_SEPARATOR +
//                    fileUploaderConfiguration.getMainDirName() +
//                    SEPARATOR +
//                    currentDirName +
//                    SEPARATOR +
//                    fileData.getFileName())) {
//
//                out.write(fileData.getFileBytes());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

}

//    @Override
//    public FileData uploadFileFromApplicationDir(FileData fileData) {
//        FileData currentFileData = null;
//        val currentDirName = getCurrentDirName(fileData);
//        val fileName = fileData.getFileName();
//        val hash = fileData.getHash();
//        try (val out = new ObjectInputStream(new FileInputStream(
//                ROOT_DIR_SEPARATOR +
//                        fileUploaderConfiguration.getMainDirName() +
//                        SEPARATOR +
//                        currentDirName +
//                        SEPARATOR +
//                        fileName +
//                        fileType
//        ))) {
//
//            currentFileData = (FileData) out.readObject();
//            if (!currentFileData.getHash().equals(hash))
//                throw new IllegalArgumentException();
//            else return currentFileData;
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return currentFileData;
//    }

//    @Override
//    public void deleteFile(FileContainer fileContainer) {
//
//    }
//
//    @Override
//    public void renameFile(FileContainer fileContainer) {
//
//    }
