package ru.netology.cloud_service_app.services;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.exceptions.DeleteFileException;
import ru.netology.cloud_service_app.exceptions.DownloadFileException;
import ru.netology.cloud_service_app.exceptions.EditFileException;
import ru.netology.cloud_service_app.exceptions.SaveFileException;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.repositories.FileRepository;
import ru.netology.cloud_service_app.utils.FileUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileServiceTestConfig.class)
class FileServiceTest {
    private static final String LOGIN = "max@mail.ru";
    private static final String FILENAME = "111.jpeg";
    private static final int SIZE = 1000;

    @Autowired
    FileService fileService;

    @MockBean
    FileRepository fileRepository;

    @Test
    void getAllFiles() {
        val limit = 1;
        val currentAllFiles = List.of(new FileData(FILENAME, SIZE));
        val files = List.of(UploadedFile.builder().name(FILENAME).size(SIZE).build());
        when(fileRepository.findAllByUserLoginAndStatus(LOGIN, ACTIVE, PageRequest.of(0, limit))).thenReturn(files);
        val allFilesFromDb = fileService.getAllFiles(limit, LOGIN);
        val isEquals = isFileDataListEquals(currentAllFiles, allFilesFromDb);
        Assertions.assertTrue(isEquals);
    }

    private boolean isFileDataListEquals(List<FileData> validFileDataList,
                                         List<FileData> fileDataListFromDb) {
        if (validFileDataList.size() == fileDataListFromDb.size()) {
            int counter = 0;
            for (FileData fileData : validFileDataList) {
                if (!fileData.equals(fileDataListFromDb.get(counter++))) return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Test
    void saveFileShouldThrowSaveFileException() {
        val fileBody = new byte[1024];
        val hash = FileUtils.getFileHash(FILENAME, LOGIN);
        val fullFileHash = FileUtils.getFullFileHash(FILENAME, LOGIN, fileBody);
        val optionalUploadedFile = Optional.of(UploadedFile.builder().hash(hash).fullFileHash(fullFileHash).status(ACTIVE).build());
        when(fileRepository.findByHash(anyString())).thenReturn(optionalUploadedFile);
        Assertions.assertThrows(SaveFileException.class,
                () -> fileService.saveFile(new MockMultipartFile(FILENAME, fileBody), LOGIN));
    }

    @Test
    void downloadFile() {
        val expectedUploadedFile = UploadedFile.builder().name(FILENAME).size(SIZE).status(ACTIVE).build();
        val optionalExpectedUploadedFile = Optional.of(expectedUploadedFile);
        when(fileRepository.findByHash(anyString())).thenReturn(optionalExpectedUploadedFile);
        val uploadedFileFromUserService = fileService.downloadFile(FILENAME, LOGIN);
        Assertions.assertEquals(expectedUploadedFile, uploadedFileFromUserService);
    }

    @Test
    void downloadFileMethodShouldThrowDownloadFileException() {
        when(fileRepository.findByHash(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(DownloadFileException.class, () -> fileService.downloadFile(FILENAME, LOGIN));
    }

    @Test
    void editFilenameShouldThrowEditFileException() {
        val newFilename = "555.jpeg";
        when(fileRepository.findByHash(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(EditFileException.class, () -> fileService.editFilename(FILENAME, newFilename, LOGIN));

    }

    @Test
    void deleteFileShouldThrowDeleteFileException() {
        when(fileRepository.findByHash(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(DeleteFileException.class, () -> fileService.deleteFile(FILENAME, LOGIN));
    }
}