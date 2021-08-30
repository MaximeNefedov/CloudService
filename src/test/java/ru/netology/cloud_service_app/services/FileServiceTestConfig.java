package ru.netology.cloud_service_app.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.netology.cloud_service_app.repositories.FileRepository;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class FileServiceTestConfig {

    @Bean
    public FileService fileService() {
        return new FileServiceImpl(fileRepository());
    }

    @Bean
    public FileRepository fileRepository() {
        return mock(FileRepository.class);
    }
}
