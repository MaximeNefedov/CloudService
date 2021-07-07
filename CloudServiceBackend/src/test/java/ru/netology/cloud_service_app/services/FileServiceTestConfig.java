package ru.netology.cloud_service_app.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.netology.cloud_service_app.models.file_uploaders.DefaultFileHandler;
import ru.netology.cloud_service_app.models.file_uploaders.FileHandler;
import ru.netology.cloud_service_app.repositories.file_repositories.CloudServiceFileRepository;
import ru.netology.cloud_service_app.repositories.file_repositories.DefaultCloudServiceFileRepository;
import ru.netology.cloud_service_app.repositories.user_repositories.CloudServiceUserRepository;
import ru.netology.cloud_service_app.repositories.user_repositories.DefaultCloudServiceUserRepository;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class FileServiceTestConfig {

    @Bean
    public FileService fileService(CloudServiceFileRepository cloudServiceFileRepository,
                                   CloudServiceUserRepository cloudServiceUserRepository) {
        return new FileService(cloudServiceFileRepository, cloudServiceUserRepository);
    }

    @Bean
    public CloudServiceFileRepository cloudServiceFileRepository() {
        return new DefaultCloudServiceFileRepository(defaultFileHandler());
    }

    @Bean
    public FileHandler defaultFileHandler() {
        return mock(DefaultFileHandler.class);
    }

    @Bean
    public CloudServiceUserRepository cloudServiceUserRepository() {
        return mock(DefaultCloudServiceUserRepository.class);
    }
}