package ru.netology.cloud_service_app.models.dbhandlers;

import lombok.val;
import org.springframework.stereotype.Component;
import ru.netology.cloud_service_app.repositories.file_repositories.FileRepository;

import static ru.netology.cloud_service_app.entities.UploadedFileStatus.DELETED;

@Component
public class DbHandlerImpl implements DbHandler {
    private final FileRepository fileRepository;

    public DbHandlerImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public boolean isFileWasDeletedRecently(String hash) {
        val fileStatus = fileRepository.getFileStatusByHash(hash);
        if (fileStatus.isEmpty()) {
            return false;
        }
        return DELETED.name().equals(fileStatus.get().name());
    }
}
