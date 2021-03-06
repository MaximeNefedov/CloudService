package ru.netology.cloud_service_app.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.UploadedFileStatus;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findAllByUserLoginAndStatus(String login, UploadedFileStatus status, Pageable pageable);

    Optional<UploadedFile> findByHash(String hash);

    void deleteByHash(String hash);
}
