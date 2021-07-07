package ru.netology.cloud_service_app.repositories.file_repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.UploadedFileStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findAllByUserLoginAndStatus(String login, UploadedFileStatus status, Pageable pageable);

    Optional<UploadedFile> findByHash(String hash);

    @Query(value = "SELECT file.status from files file where file.hash = :hash")
    Optional<UploadedFileStatus> getFileStatusByHash(String hash);

//    @Modifying
//    @Query(value = "UPDATE files file set file.hash = :newHash, file.name = :newFilename where file.hash = :oldHash")
//    void editFilename(String oldHash, String newHash, String newFilename);

    @Modifying
    @Query(value = "UPDATE files file set file.status = :fileStatus, file.removalTime = null where file.hash = :hash")
    void setActiveFileStatus(String hash, UploadedFileStatus fileStatus);

//    @Modifying
//    @Query(value = "UPDATE files file set file.status = :fileStatus, file.removalTime = :localDateTime where file.hash = :hash")
//    void setDeletedFileStatus(String hash, UploadedFileStatus fileStatus, LocalDateTime localDateTime);
}
