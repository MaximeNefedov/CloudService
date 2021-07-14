package ru.netology.cloud_service_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import ru.netology.cloud_service_app.models.FileData;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        final ClassPathResource classPathResource = new ClassPathResource("1.jpeg");
        try (val in = new BufferedInputStream(classPathResource.getInputStream())) {
            System.out.println(in.readAllBytes().length);
        }
    }
}
