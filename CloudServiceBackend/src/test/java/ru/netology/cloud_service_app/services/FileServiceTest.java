//package ru.netology.cloud_service_app.services;
//
//import lombok.val;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import ru.netology.cloud_service_app.entities.UploadedFile;
//import ru.netology.cloud_service_app.exceptions.DeleteFileException;
//import ru.netology.cloud_service_app.exceptions.DownloadFileException;
//import ru.netology.cloud_service_app.exceptions.EditFileException;
//import ru.netology.cloud_service_app.exceptions.SaveFileException;
//import ru.netology.cloud_service_app.models.FileData;
//import ru.netology.cloud_service_app.repositories.handlers.file_uploaders.FileHandler;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = FileServiceTestConfig.class)
//class FileServiceTest {
//    private static final String USERNAME = "max@mail.ru";
//    private static final String FILENAME = "111.jpeg";
//    private static final int SIZE = 1000;
//
//    @Autowired
//    FileService fileService;
//
//    @Autowired
//    FileHandler fileHandler;
//
//    @Test
//    void getAllFiles() {
//        val limit = 1;
//        val currentAllFiles = List.of(new FileData(FILENAME, SIZE));
//        val files = List.of(UploadedFile.builder().name(FILENAME).size(SIZE).build());
//        System.out.println(files);
//
//        when(fileHandler.getAllFiles(limit, USERNAME)).thenReturn(files);
//        val allFilesFromDb = fileService.getAllFiles(limit, USERNAME);
//        System.out.println(allFilesFromDb);
//        val isEquals = isFileDataListEquals(currentAllFiles, allFilesFromDb);
//        Assertions.assertTrue(isEquals);
//    }
//
//    private boolean isFileDataListEquals(List<FileData> validFileDataList,
//                                         List<FileData> fileDataListFromDb) {
//        if (validFileDataList.size() == fileDataListFromDb.size()) {
//            int counter = 0;
//            for (FileData fileData : validFileDataList) {
//                if (!fileData.equals(fileDataListFromDb.get(counter++))) return false;
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Test
//    void saveFileShouldThrowSaveFileException() {
//        when(fileHandler.uploadToDb(any(), any())).thenThrow(SaveFileException.class);
//        Assertions.assertThrows(SaveFileException.class,
//                () -> fileService.saveFile(null, USERNAME));
//    }
//
//    @Test
//    void downloadFile() {
//        val expectedUploadedFile = UploadedFile.builder().name(FILENAME).size(SIZE).status(ACTIVE).build();
//        val optionalExpectedUploadedFile = Optional.of(expectedUploadedFile);
//        when(fileHandler.downloadFile(FILENAME, USERNAME)).thenReturn(optionalExpectedUploadedFile);
//        val uploadedFileFromUserService = fileService.downloadFile(FILENAME, USERNAME);
//        Assertions.assertEquals(expectedUploadedFile, uploadedFileFromUserService);
//    }
//
//    @Test
//    void downloadFileMethodShouldThrowDownloadFileException() {
//        when(fileHandler.downloadFile(FILENAME, USERNAME)).thenReturn(Optional.empty());
//        Assertions.assertThrows(DownloadFileException.class, () -> fileService.downloadFile(FILENAME, USERNAME));
//    }
//
//    @Test
//    void editFilenameShouldThrowEditFileException() {
//        val newFilename = "555.jpeg";
//        when(fileHandler.editFilenameInDb(FILENAME, newFilename, USERNAME)).thenReturn(false);
//        Assertions.assertThrows(EditFileException.class,
//                () -> fileService.editFilename(FILENAME, newFilename, USERNAME));
//
//    }
//
//    @Test
//    void deleteFileShouldThrowDeleteFileException() {
//        when(fileHandler.deleteFileFromDb(FILENAME, USERNAME)).thenReturn(false);
//        Assertions.assertThrows(DeleteFileException.class,
//                () -> fileService.deleteFile(FILENAME, USERNAME));
//    }
//}