//package ru.netology.cloud_service_app;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import ru.netology.cloud_service_app.entities.UploadedFile;
//import ru.netology.cloud_service_app.models.file_uploaders.FileUploader;
//import ru.netology.cloud_service_app.repositories.FileRepository;
//import ru.netology.cloud_service_app.repositories.UserRepository;
//
//import javax.transaction.Transactional;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Component
//public class AppRunner implements CommandLineRunner {
//    private final UserRepository userRepository;
//    private final FileRepository fileRepository;
//    private final FileUploader fileUploader;
//
//    public AppRunner(UserRepository userRepository, FileRepository fileRepository, FileUploader fileUploader) {
//        this.userRepository = userRepository;
//        this.fileRepository = fileRepository;
//        this.fileUploader = fileUploader;
//    }
//
//    @Transactional
//    @Override
//    public void run(String... args) throws Exception {
//
//    }
//}
