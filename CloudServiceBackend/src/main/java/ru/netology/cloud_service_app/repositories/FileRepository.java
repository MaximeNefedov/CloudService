package ru.netology.cloud_service_app.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloud_service_app.entities.UploadedFile;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findFileByUserLogin(String login, Pageable pageable);
    Optional<UploadedFile> findByNameAndUserLogin(String filename, String login);
}
