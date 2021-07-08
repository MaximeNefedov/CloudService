package ru.netology.cloud_service_app.handlers.dbhandlers;

import lombok.val;
import org.springframework.stereotype.Component;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.repositories.file_repositories.FileRepository;

import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;
import static ru.netology.cloud_service_app.entities.UploadedFileStatus.DELETED;

@Component
public class DbHandlerImpl implements DbHandler {
    private final FileRepository fileRepository;

    public DbHandlerImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public boolean isFileAbleToBeRestored(String hash, byte[] fileBites) {
        val file = fileRepository.findByHashAndStatus(hash, DELETED);
        if (file.isEmpty()) {
            return false;
        } else {
            val uploadedFile = file.get();
            val fileBody = uploadedFile.getFileBody();
            if (isSameFile(fileBody, fileBites)) {
                uploadedFile.setStatus(ACTIVE);
                return true;
            } else {
                fileRepository.deleteByHash(hash);
                fileRepository.flush();
                return false;
            }
        }
    }

    private boolean isSameFile(byte[] originalFileBytes, byte[] newFileBytes) {
        if (originalFileBytes.length != newFileBytes.length) return false;
        int counter = 0;
        for (byte originalFileByte : originalFileBytes) {
            if (originalFileByte != newFileBytes[counter++]) return false;
        }
        return true;
    }
}
