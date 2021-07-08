package ru.netology.cloud_service_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.netology.cloud_service_app.configs.FileUploaderConfiguration;
import ru.netology.cloud_service_app.handlers.file_uploaders.FileHandler;
import ru.netology.cloud_service_app.repositories.file_repositories.FileRepository;
import ru.netology.cloud_service_app.repositories.user_repositories.UserRepository;
import ru.netology.cloud_service_app.services.FileService;

@Component
public class AppRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;
    private final FileUploaderConfiguration configuration;
    private final FileService fileService;
    private final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    public AppRunner(UserRepository userRepository,
                     FileRepository fileRepository,
                     FileHandler fileHandler,
                     FileUploaderConfiguration configuration,
                     FileService fileService) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.fileHandler = fileHandler;
        this.configuration = configuration;
        this.fileService = fileService;
    }

    @Override
    public void run(String... args) throws Exception {
//        final UploadedFile file = fileService.downloadFile("scan.pdf", "max@mail.ru");
//        Files.write(Path.of("test.pdf"), file.getFileBody());
    }
}
